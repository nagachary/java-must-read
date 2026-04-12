# Distributed Rate Limiter — System Design

**Version:** 1.0  
**Scope:** Single-region deployment enforcing per-user API limits across a distributed application server fleet  


---

## Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Use Case and Requirements](#2-use-case-and-requirements)
3. [Constraints and Scale Parameters](#3-constraints-and-scale-parameters)
4. [Algorithm Selection](#4-algorithm-selection)
5. [Architecture Overview](#5-architecture-overview)
6. [Component Design](#6-component-design)
7. [Data Store Selection — Redis](#7-data-store-selection--redis)
8. [Redis Data Model](#8-redis-data-model)
9. [Reverse Proxy Approach](#9-reverse-proxy-approach)
10. [Request Flow — End to End](#10-request-flow--end-to-end)
11. [Race Condition Analysis and Mitigation](#11-race-condition-analysis-and-mitigation)
12. [High Availability Design](#12-high-availability-design)
13. [Failure Handling](#13-failure-handling)
14. [CAP Theorem Position](#14-cap-theorem-position)
15. [Back-of-Envelope Estimation](#15-back-of-envelope-estimation)
16. [Advantages](#16-advantages)
17. [Trade-offs and Limitations](#17-trade-offs-and-limitations)
18. [Open Extensions](#18-open-extensions)
19. [Diagram](#19-diagram)

---

## 1. Problem Statement

A public REST API is served from a fleet of 50 application servers within a single region. Each server independently handles a portion of the incoming traffic through load balancing, with no shared local state between servers. Without a coordinated rate limiting mechanism, a single user can exceed their permitted request allocation simply by having their requests distributed across multiple servers — each server would see only a fraction of the user's total traffic and would incorrectly conclude that the limit has not been reached.

The system must enforce a consistent per-user rate limit regardless of which server handles any individual request, maintain sub-millisecond enforcement overhead on the critical request path, and remain available to legitimate users even when rate limiting infrastructure experiences partial or complete failure.

---

## 2. Use Case and Requirements

### Functional Requirements

The system enforces a limit of 1,000 requests per minute per API key across all servers in the fleet. Every request exceeding this limit receives an HTTP 429 Too Many Requests response with standard rate limit headers communicating the client's current quota status and the time at which their limit resets. Every permitted request receives the same headers, allowing well-behaved clients to self-throttle before reaching the limit. The rate limit identifier is the API key carried in the request header, with IP address serving as a secondary identifier for unauthenticated requests arriving without a valid key.

### Non-Functional Requirements

The rate limiter must add no more than 5 milliseconds of additional latency to any request on the critical path. The system must remain available to legitimate users under infrastructure failure, prioritising availability over strict enforcement consistency. The rate limiting infrastructure must recover automatically from node failures without manual intervention. The solution must be horizontally scalable as fleet throughput grows.

### Out of Scope

Multi-region enforcement, per-endpoint rate limits, and user-level aggregation across multiple API keys are explicitly out of scope for this design version and are noted as future extensions.

---

## 3. Constraints and Scale Parameters

| Parameter                | Value                                      | Derivation                    |
|--------------------------|--------------------------------------------|-------------------------------|
| Deployment scope         | Single region                              | Clarified requirement         |
| Application server count | 50 servers                                 | Clarified requirement         |
| Peak fleet throughput    | 100,000 requests per second                | Clarified requirement         |
| Per-server throughput    | ~2,000 requests per second average         | 100,000 / 50 servers          |
| Rate limit               | 1,000 requests per minute per API key      | Clarified requirement         |
| Per-user sustained rate  | ~16.7 requests per second                  | 1,000 / 60 seconds            |
| Active user population   | 10 million users                           | Clarified requirement         |
| Maximum Redis entries    | 20 million                                 | 2 counters × 10 million users |
| Maximum latency overhead | 5 milliseconds                             | Clarified requirement         |
| Failure mode             | Fail open                                  | Clarified requirement         |
| Recovery mechanism       | Automatic via Sentinel and circuit breaker | Design decision               |

The distinction between per-server throughput and per-user rate limit is architecturally significant. The 2,000 requests per second that each server receives is the aggregate of traffic from thousands of concurrent users — not traffic from a single user. The per-user limit of 1,000 per minute equates to approximately 16.7 requests per second per user at sustained maximum usage, a figure far below the per-server throughput because the server simultaneously serves many users in parallel.

---

## 4. Algorithm Selection

### Candidate Algorithms Evaluated

Five rate limiting algorithms were evaluated against the stated requirements: Fixed Window Counter, Sliding Window Log, Sliding Window Counter, Token Bucket, and Leaky Bucket. Each was assessed across accuracy, memory consumption, burst handling, computational cost, and suitability for the stated use case.

The Fixed Window Counter was eliminated due to its boundary vulnerability, which allows clients to receive double their permitted allocation by timing requests around the window reset boundary. While operationally simple, this vulnerability is inconsistent with a system intended for abuse prevention.

The Sliding Window Log was eliminated due to its memory consumption characteristics. Storing one timestamp per request per user at 100,000 requests per second would produce memory requirements incompatible with the operational envelope of a Redis deployment at this scale.

The Token Bucket was evaluated and found appropriate for endpoints where burst accommodation is a design goal. However, for a general-purpose public API where consistent per-minute enforcement is the intent, the token bucket's explicit burst accumulation — which allows idle clients to receive substantially more than the per-minute limit in a short period — is a less precise fit than the sliding window counter.

The Leaky Bucket was eliminated because it introduces deliberate processing delay through its queuing model, which is incompatible with a synchronous request-response API expecting immediate responses.

### Selected Algorithm — Sliding Window Counter

The Sliding Window Counter was selected as the primary rate limiting algorithm. It delivers near-exact enforcement at constant memory cost, eliminates the boundary vulnerability of the fixed window counter, and is the algorithm used in production by Cloudflare, Stripe, and most major API platforms for equivalent use cases.

### How the Algorithm Works

The algorithm maintains two integer counters per user per window cycle — one for the current window and one for the immediately preceding window. When a request arrives, the Rate Limiter Service computes a weighted estimate of total recent request volume using the following formula.

```
estimated = (previous_count × overlap_ratio) + current_count

overlap_ratio = (window_duration - elapsed_time) / window_duration
```

The overlap ratio expresses what proportion of the previous window still falls within the trailing window-duration period measured from the current moment. As the current window advances, this ratio decreases continuously from 1.0 at the window boundary to 0.0 at the end of the window, producing a smooth estimate with no abrupt reset point that a client could exploit.

### Why No Boundary Vulnerability

A fixed window counter resets to zero at a predetermined boundary, creating a known moment at which a client can send requests immediately before and immediately after to receive double their allocation. The sliding window counter has no such moment. The previous window's contribution to the estimate diminishes continuously as the current window advances, meaning there is no discrete reset event and no exploitable boundary.

---

## 5. Architecture Overview

```
Client
  |
  | HTTP Request + API Key Header
  v
+------------------------------------------+
|            Reverse Proxy                  |
|   (Horizontally Scaled, Multiple Instances)|
|                                           |
|  +------------------+                    |
|  | Redis Circuit    |<---> Redis Sentinel |
|  | Breaker          |                    |
|  +------------------+                    |
|          |                               |
|  +------------------+                    |
|  | Rate Limiter Srv |                    |
|  | Sliding Window   |-----> Redis        |
|  | Algorithm        |       Cluster      |
|  +------------------+       Primary      |
|                                |         |
+------------------------------------------+
  |                              |
  | Permitted Request     Push Replication
  v                              v
Load Balancer            Redis Cluster
Round Robin              Replica
  |
  +---> App Server 1
  +---> App Server 2
  +---> App Server 3
  ...
  +---> App Server 50
  |
  v
Response back through Reverse Proxy to Client
(200 OK or 429 Too Many Requests)
```

---

## 6. Component Design

### Reverse Proxy

The reverse proxy is the entry point for all client traffic and the layer at which rate limit enforcement occurs before any application server is involved. It is deployed as multiple horizontally scaled instances, each independently capable of enforcing rate limits by consulting the shared Redis cluster. No coordination between reverse proxy instances is required because all shared state resides in Redis.

The reverse proxy contains two sub-components relevant to rate limiting: the Redis Circuit Breaker and the Rate Limiter Service.

### Rate Limiter Service

The Rate Limiter Service is responsible for the complete enforcement sequence on every incoming request. It extracts the API key from the request header, constructs the Redis keys for the current and previous windows, reads the previous window counter, issues the atomic increment against the current window key, applies the EXPIRE command on first key creation, computes the weighted sliding window estimate, and makes the allow-or-reject decision. It populates the standard rate limit response headers on every request — permitted and rejected alike — and returns HTTP 429 directly to the client for rejected requests without forwarding to the load balancer.

### Redis Circuit Breaker

The circuit breaker monitors the health of the connection between the Rate Limiter Service and the Redis cluster. When consecutive Redis calls fail or time out — indicating that Redis is unreachable — the circuit breaker opens and instructs the Rate Limiter Service to enter fail-open mode, permitting all requests without enforcement. The circuit breaker periodically probes Redis to detect recovery and closes automatically once connectivity is confirmed, restoring normal enforcement without manual intervention. The circuit breaker timeout is configured to open before the Redis call itself consumes the full 5-millisecond latency budget, ensuring that a degraded Redis response does not cause the rate limiter to breach its latency contract.

### Load Balancer

The load balancer is a separate component from the reverse proxy, receiving only requests that the Rate Limiter Service has permitted. It distributes permitted requests across the 50 application servers using round-robin routing. Because rate limit state is stored in Redis rather than on any individual server, no sticky routing or consistent hashing is required — any server can handle any permitted request.

### Redis Cluster

The Redis cluster serves as the shared counter store for all rate limit state. It is deployed as a primary node with one or more replicas, managed by Redis Sentinel for automatic failover. Redis is responsible for storing counter values, enforcing TTL expiry, and returning post-increment values atomically. No rate limiting business logic resides in Redis — it receives and executes standard Redis commands issued by the Rate Limiter Service.

### Redis Sentinel

Redis Sentinel monitors the health of both the primary and replica Redis nodes. When the primary becomes unavailable, Sentinel reaches a quorum decision among its own instances, promotes the replica to assume the primary role, and notifies connected clients of the topology change. This process completes automatically within 10 to 30 seconds depending on configuration, eliminating the need for manual intervention during Redis node failures.

### Application Servers

The 50 application servers handle business logic for permitted requests. They have no awareness of the rate limiting layer above them and require no modification to participate in the rate-limited architecture.

---

## 7. Data Store Selection — Redis

### Why Redis

Redis was selected as the shared counter store based on four characteristics that align precisely with the requirements of this design.

Redis provides sub-millisecond read and write latency for simple key-value operations within the same data centre. A single Redis round trip completes in 1 to 2 milliseconds under normal load, leaving headroom within the 5-millisecond budget for the Rate Limiter Service's local computation and the overhead of constructing the HTTP response. No other data store category offers this combination of throughput and latency for key-value operations at the required scale.

Redis's `INCR` command is atomic at the command level. Two concurrent servers issuing `INCR` against the same key will always receive two distinct post-increment values, eliminating the most severe form of the read-modify-write race condition without requiring application-level locking or distributed coordination. This atomicity is the foundational correctness guarantee of the rate limiting enforcement path.

Redis supports key-level TTL natively, allowing the Rate Limiter Service to set expiry at key creation time and delegate all subsequent lifecycle management to Redis. Expired keys are deleted automatically and asynchronously by Redis's internal expiry mechanism without any application involvement, preventing unbounded memory growth without the operational overhead of a dedicated cleanup process.

Redis handles the 200,000 operations per second required by this design (100,000 writes and 100,000 reads at peak) within the capacity of a single well-provisioned node. The counter values stored are plain integers — among the cheapest data structures Redis manages — and the key strings are short enough that memory consumption remains well within a standard provisioning envelope.

### Why Not a Relational Database

A relational database such as PostgreSQL or Oracle supports atomic increment through row-level locking and `UPDATE RETURNING` semantics, which is semantically compatible with the enforcement pattern. However, relational databases enforce ACID guarantees through write-ahead logging, lock management, and disk persistence, introducing overhead that places individual write latency in the 2 to 10 millisecond range. At 100,000 write operations per second, this would require either an impractically large database cluster or would systematically breach the 5-millisecond latency budget. Relational databases are appropriate for rate limiting in lower-traffic environments below approximately 1,000 requests per second.

### Portability Consideration

Because the sliding window counter business logic resides entirely in the Rate Limiter Service rather than in Redis scripts, the storage layer is replaceable. Any data store that provides a genuinely atomic increment returning the post-increment value, a TTL or equivalent expiry mechanism, and sufficient throughput within the latency budget — such as Memcached, Apache Ignite, or Hazelcast — could serve as a drop-in replacement with changes confined to the data access layer of the Rate Limiter Service.

---

## 8. Redis Data Model

### Key Structure

The Rate Limiter Service constructs two Redis keys per active user at any given time, assembled from three components.

```
apikey:<api_key_value>:window:<window_id>
```

The `apikey` prefix namespaces all rate limit counters within Redis, preventing collision with any other data residing in the same cluster. The `<api_key_value>` component is the credential extracted from the incoming HTTP request header, uniquely identifying the client. The `<window_id>` component is derived by dividing the current epoch timestamp in milliseconds by the window duration in milliseconds, producing a monotonically increasing integer that is identical for all timestamps falling within the same window boundary and increments by exactly one at each window transition.

All 50 reverse proxy instances independently derive the same window ID for requests arriving within the same window, using their locally synchronised clocks without any inter-instance coordination.

### Stored Fields

```
Current Window Key:  apikey:<api_key_value>:window:<window_id>
Value:               Integer — request count for the active window
TTL:                 120 seconds (twice the 60-second window duration)
Written by:          Rate Limiter Service via INCR command
Read by:             Rate Limiter Service (post-increment value)

Previous Window Key: apikey:<api_key_value>:window:<window_id - 1>
Value:               Integer — request count from preceding window
TTL:                 120 seconds (set when this key was the current key)
Written by:          Rate Limiter Service (during its own active window)
Read by:             Rate Limiter Service (for weighted estimate calculation)
```

### Concrete Example

For API key `xyz789` at epoch time 1,680,000,045,000 milliseconds against a 60,000-millisecond window:

```
Window ID        = 1,680,000,045,000 / 60,000 = 28,000,000

Current key:     apikey:xyz789:window:28000000  = 742   TTL: 98s
Previous key:    apikey:xyz789:window:27999999  = 389   TTL: 38s
```

### TTL Lifecycle

The TTL is set to 120 seconds — twice the window duration — ensuring that a key persists through both its own active window and the immediately following window, during which it serves as the previous window counter. At 120 seconds, Redis deletes the key automatically. By this point, the key is two windows in the past and will never be read again by any rate limit evaluation. The `EXPIRE` command is issued by the Rate Limiter Service only when the post-increment value from `INCR` equals one, indicating the key was just created by this increment. All subsequent increments within the same window leave the TTL unchanged.

---

## 9. Reverse Proxy Approach

### Why Business Logic Resides in the Reverse Proxy

The architectural decision to place the sliding window counter logic in the reverse proxy rather than in Redis as a Lua script was deliberate and reflects three engineering principles.

Separation of concerns is the primary driver. When rate limiting logic resides in a Redis script, business rules are embedded in infrastructure. Any change to the algorithm — adjusting the formula, adding a secondary limit, or modifying the window duration — requires modifying and redeploying Redis configuration rather than application code. Placing the logic in the reverse proxy means it is version-controlled alongside the application, deployable through standard continuous delivery pipelines, and testable independently of the Redis deployment.

Resilience under partial failure is the second driver. With business logic in the reverse proxy, the circuit breaker can degrade gracefully to a fail-open state when Redis is unavailable, and can optionally read from the Redis replica for counter values when the primary is unreachable. When logic and data both reside in Redis, a Redis failure takes down both simultaneously, leaving no degraded operating mode available. With logic in the reverse proxy, only the data access is interrupted — the algorithm itself continues to execute, either against replica data or in fail-open mode.

Portability is the third driver. Because Redis is used only as a counter store — receiving standard `INCR`, `GET`, and `EXPIRE` commands — the storage layer can be replaced with any compatible alternative without modifying the rate limiting algorithm. This decoupling gives the system the flexibility to migrate to a different caching infrastructure as requirements evolve.

### Atomicity Without Lua Scripts

Moving business logic to the reverse proxy introduces a gap between the Redis read and write operations that a Lua script would execute atomically. This gap creates the possibility of concurrent servers both reading the same counter value, both computing an estimate below the limit, and both incrementing — allowing excess requests to pass through. The design resolves this through atomic increment with post-write validation, using Redis's native `INCR` command rather than a read-then-conditional-write sequence. Since `INCR` is atomic, two concurrent servers incrementing the same key always receive two distinct post-increment values. The Rate Limiter Service then evaluates the returned value against the limit and accepts any transient over-count as an acceptable margin given the fair usage enforcement context.

---

## 10. Request Flow — End to End

### Permitted Request

The client issues an HTTP request to the API endpoint, including their API key in the request header. The reverse proxy receives the request and the Rate Limiter Service extracts the API key value. The Rate Limiter Service constructs the current and previous window key strings using the current epoch timestamp and issues a `GET` against the previous window key to retrieve the prior counter value. It issues `INCR` against the current window key and receives the post-increment value. If the post-increment value equals one, it immediately issues `EXPIRE` against the current window key, setting the TTL to 120 seconds. It computes the weighted sliding window estimate using the previous counter, the post-increment current counter, and the elapsed time within the current window. The estimate is below the limit — the Rate Limiter Service attaches the standard rate limit headers to the request and forwards it to the load balancer. The load balancer routes the request to one of the 50 application servers via round-robin distribution. The application server processes the business logic and returns a response through the reverse proxy to the client with HTTP 200 OK.

### Rejected Request

The sequence proceeds identically through the estimate computation. The estimate equals or exceeds the limit. The Rate Limiter Service returns HTTP 429 Too Many Requests directly to the client, populating the `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`, and `Retry-After` headers. The request is never forwarded to the load balancer or any application server, consuming no downstream capacity.

### Fail-Open Request

The Rate Limiter Service issues the Redis commands and the circuit breaker detects that Redis is unreachable — either because the primary has failed and Sentinel's failover transition is in progress, or because of a broader cluster outage. The circuit breaker opens, the Rate Limiter Service skips all Redis operations, and the request is forwarded to the load balancer as permitted without any rate limit evaluation. This condition persists until the circuit breaker detects Redis recovery and closes, at which point normal enforcement resumes automatically.

---

## 11. Race Condition Analysis and Mitigation

### The Race Condition

When the sliding window logic resides in the reverse proxy and multiple servers concurrently process requests for the same user at or near the limit, a read-compute-write race condition exists. Two servers may read identical counter values, both compute estimates below the limit, and both issue increments — allowing excess requests to pass through. This is an inherent property of any distributed read-modify-write sequence that is not protected by a single atomic operation encompassing all three steps.

### Why the DECR Approach Introduces a Secondary Risk

The intuitive resolution — issuing a `DECR` command when the post-increment value exceeds the limit — introduces a secondary race condition. If two servers concurrently detect an over-limit condition and both issue `DECR`, both decrements execute against the same counter, producing an under-count that allows subsequent requests to pass when they should be rejected. Under high concurrent load, this secondary race condition can cause the counter to diverge significantly from the true permitted request count.

### Selected Mitigation

The design eliminates the `DECR` operation entirely and accepts a small transient over-count margin. This decision is consistent with the three established characteristics of the system: the sliding window counter algorithm is itself an approximation, the fail-open behaviour accepts brief enforcement gaps during infrastructure failure, and the rate limit serves fair usage enforcement rather than a hard security boundary. The counter self-corrects at each window boundary, and the practical impact of occasional concurrent over-counting under peak load is negligible at the traffic volumes and limit parameters of this design.

For systems where any over-count is operationally unacceptable, Redis `WATCH`, `MULTI`, and `EXEC` optimistic locking provides a corrective mechanism at the cost of retry overhead under high concurrency.

---

## 12. High Availability Design

### Redis Replication

The Redis cluster is deployed as a primary node with one or more replicas. The primary accepts all write operations — the `INCR` and `EXPIRE` commands issued by the Rate Limiter Service — and replicates data to replicas asynchronously. Replicas serve as warm standbys, maintaining a near-current copy of all counter data that can be promoted to primary without data loss under most failure scenarios.

### Redis Sentinel

Redis Sentinel is deployed as a set of monitoring processes — typically three instances to maintain quorum — that independently monitor the health of both the primary and replica Redis nodes. When a quorum of Sentinel instances agrees that the primary is unavailable — determined by the configured failure detection timeout — Sentinel initiates automatic failover: it selects the most up-to-date replica, promotes it to primary, and notifies all connected clients of the new topology. This process completes within 10 to 30 seconds depending on configuration, after which the Rate Limiter Service reconnects to the new primary and resumes normal enforcement.

### Circuit Breaker Interaction

The circuit breaker and Redis Sentinel address different failure scenarios and are complementary rather than redundant. Redis Sentinel addresses node-level failure — recovering the Redis infrastructure automatically. The circuit breaker addresses application-level behaviour during any period, including the Sentinel failover transition window, when Redis is unreachable from the reverse proxy's perspective. Without the circuit breaker, the Rate Limiter Service would block on Redis call timeouts for every request during the failover window, producing catastrophic latency degradation across the entire fleet. The circuit breaker prevents this by detecting the failure quickly and switching to fail-open mode, keeping the API responsive throughout the outage.

---

## 13. Failure Handling

### Redis Primary Node Failure

The Redis primary fails and Sentinel begins the failover process. During the transition window of 10 to 30 seconds, all Rate Limiter Service calls to Redis time out. The circuit breaker detects consecutive failures, opens, and switches the Rate Limiter Service to fail-open mode — all requests are permitted without enforcement. Sentinel completes the failover, promotes the replica to primary, and notifies clients. The circuit breaker detects that Redis is reachable again, closes, and the Rate Limiter Service resumes normal enforcement. Counters written to the new primary during recovery may reflect a brief gap corresponding to the failover window, but this corrects naturally as the window advances.

### Full Cluster Outage

Both primary and replica are unreachable. The circuit breaker opens immediately upon detecting the failure and the Rate Limiter Service operates in fail-open mode indefinitely. Legitimate users experience no service interruption. Enforcement resumes automatically when the cluster recovers and the circuit breaker closes.

### Slow Redis Degradation

Redis responds to commands but with latency exceeding the circuit breaker timeout threshold. The circuit breaker opens before the full 5-millisecond latency budget is consumed, preventing the rate limiter from breaching its own latency contract. Requests are permitted in fail-open mode until Redis latency returns to normal operating levels.

---

## 14. CAP Theorem Position

This design occupies the AP quadrant of the CAP theorem — it guarantees Availability and Partition Tolerance while accepting eventual consistency and approximate enforcement as trade-offs.

### Why AP Is Correct for This Use Case

The business requirement established during design is that availability takes priority over strict enforcement. The fail-open behaviour is the defining CAP decision of the architecture. When Redis is unreachable, the system remains available to all users rather than blocking legitimate traffic to preserve enforcement integrity. This reflects a deliberate assessment that the harm of blocking 10 million users during a Redis outage materially exceeds the harm of briefly permitting abusive traffic to exceed its limit.

Under normal operation, the design accepts eventual consistency in two forms. The sliding window counter's weighted formula is an approximation that assumes uniform request distribution across the previous window. The atomic increment with post-write validation eliminates the most severe concurrent over-counting scenario but accepts small transient deviations under peak concurrency rather than introducing the latency overhead of distributed consensus operations.

### Implications

The AP position means this design is appropriate for general-purpose API rate limiting where the rate limit is an abuse prevention and fair usage control. It is not appropriate for security-critical contexts — authentication endpoints, payment APIs, or account lockout mechanisms — where any over-count, however brief, constitutes a meaningful security risk. Those contexts require a CP-positioned design with stronger consistency guarantees, at the cost of higher latency and reduced availability under failure.

---

## 15. Back-of-Envelope Estimation

### Traffic Volume and Redis Operation Rate

At 100,000 requests per second fleet-wide, every request generates exactly two Redis operations: one `INCR` write against the current window key and one `GET` read against the previous window key. The fleet therefore generates 200,000 Redis operations per second at peak load. A single well-provisioned Redis node handling simple integer operations sustains approximately 100,000 to 200,000 operations per second. The rate limiter's operations are among the cheapest Redis handles — plain integer increments and key reads with no complex data structure traversal — placing this workload comfortably within single-node capacity with headroom before horizontal sharding becomes necessary.

### Redis Memory Consumption

| Component                 | Calculation                   | Result          |
|---------------------------|-------------------------------|-----------------|
| Active users              | Given                         | 10,000,000      |
| Keys per user             | 2 (current + previous window) | 20,000,000 keys |
| Key string size           | ~40 bytes average             | 40 bytes        |
| Integer value size        | 64-bit integer                | 8 bytes         |
| Redis internal overhead   | Per-key bookkeeping           | ~22 bytes       |
| Total per key             | 40 + 8 + 22                   | ~70 bytes       |
| Total memory at full load | 20,000,000 × 70 bytes         | ~1.4 GB         |

A Redis node provisioned with 8 gigabytes of memory accommodates the full user population at peak load with approximately 5.7 gigabytes of headroom for replication buffer, operational overhead, and growth. A 16-gigabyte provisioning provides substantial buffer for traffic growth before re-evaluation is required.

### Network Bandwidth

Each Redis operation involves a payload of approximately 50 bytes in each direction — the key string at approximately 40 bytes plus command overhead of approximately 10 bytes. At 200,000 operations per second, the aggregate bandwidth between the reverse proxy fleet and the Redis cluster is 200,000 multiplied by 100 bytes, equating to 20 megabytes per second or 160 megabits per second. This is well within the capacity of a standard data centre network link and confirms that bandwidth is not a constraint at the stated traffic volume.

### Key Creation and Expiry Rate

With a 60-second window and 10 million active users, the rate at which new window keys are created at steady state is approximately 10,000,000 divided by 60, equating to roughly 167,000 key creations per second. This is within the Redis operational envelope established above. The same rate applies to key deletions via TTL expiry, as each key created in one window is deleted approximately 120 seconds later.

### Scalability Threshold

The current architecture supports single-node Redis primary capacity. The inflection point at which horizontal Redis sharding should be evaluated is approximately 150,000 fleet requests per second, at which point the Redis operation rate approaches 300,000 operations per second and begins to approach the upper bound of single-node capacity. Redis Cluster provides hash-slot-based sharding that distributes keys across multiple primary nodes, each handling a proportional share of the operation volume, allowing the architecture to scale linearly with fleet throughput growth.

---

## 16. Advantages

The separation of business logic from Redis into the reverse proxy produces several operational benefits that extend beyond the immediate design. The rate limiting algorithm is deployable and testable as standard application code, independent of Redis configuration. Changes to the algorithm — window duration, weighting formula, or limit values — are made in the application tier and deployed through standard pipelines without Redis involvement. The storage layer is replaceable with any compatible alternative without modifying the enforcement logic, providing long-term architectural flexibility.

The fail-open circuit breaker ensures that a Redis outage, however complete, is invisible to legitimate users. The API continues responding normally during infrastructure failure, and enforcement resumes automatically without operator involvement once the failure is resolved. This availability guarantee is achievable precisely because the business logic resides in the reverse proxy rather than in Redis — the algorithm continues to execute in fail-open mode even when its data store is unreachable.

The sliding window counter algorithm eliminates the boundary vulnerability that makes the fixed window counter exploitable, while requiring only two integer counters per user regardless of traffic volume. This combination of accuracy and memory efficiency is what makes the algorithm viable at 10 million active users without requiring a disproportionately large Redis cluster.

The atomic `INCR` command provides correctness under concurrent access without requiring distributed locking, Lua scripting, or application-level coordination between the 50 reverse proxy instances. Each instance independently issues the same commands against the same shared keys, and Redis's single-threaded command processing ensures that concurrent increments always produce distinct results.

---

## 17. Trade-offs and Limitations

### Soft Enforcement Ceiling Under Concurrency

The decision to eliminate the `DECR` operation and accept a small transient over-count means the rate limit is a soft ceiling rather than a hard one under peak concurrent load. Under high concurrency — many requests for the same user arriving simultaneously at or near the limit — a small number of excess requests may be permitted before the counter correctly reflects the true total. This margin is operationally acceptable for a fair usage enforcement context but would be unacceptable for a security-critical endpoint.

### Enforcement Gap During Infrastructure Failure

The fail-open behaviour produces a complete enforcement gap for the duration of any Redis outage. During the 10 to 30 second Sentinel failover window and during any broader cluster failure, all requests from all users are permitted regardless of their rate limit status. A client who monitors the API's response behaviour could detect this window and deliberately exploit it to exceed their allocation. For an API whose rate limit is a fair usage control, this risk is accepted. For a payment or authentication endpoint, it would require mitigation through a local in-memory fallback counter or a CP-positioned alternative.

### Algorithm Approximation Error

The sliding window counter's weighted formula assumes that requests in the previous window were uniformly distributed across that window. When requests are heavily clustered — for example, all 1,000 requests arriving in the final second of the previous window — the formula underestimates the true recent request density at the window boundary. This approximation error is typically below one percent for realistic traffic distributions and is acceptable for fair usage enforcement, but it means the algorithm cannot provide the exact enforcement guarantee of the sliding window log.

### Absence of Cross-Region Enforcement

The single-region scope of this design means that a user who routes requests through multiple geographic regions receives their limit enforced independently per region rather than globally. A global limit of 1,000 requests per minute is effectively multiplied by the number of regions through which a user can route traffic. Resolving this requires a fundamentally different consistency model — either a strongly consistent global counter with the latency implications of cross-region synchronisation, or a regional quota allocation strategy that distributes the global limit across regions at provisioning time.

---

## 18. Open Extensions

### Multi-Region Rate Limiting

Extending the design to enforce a consistent global limit across multiple geographic regions requires a cross-region coordination mechanism. Two approaches are viable depending on the consistency requirement. A regional quota allocation strategy assigns each region a proportional share of the global limit — for example, 500 requests per minute for the European region and 500 for the Asian region — enforced independently per region without cross-region communication. This approach is eventually consistent and operationally simple but requires manual rebalancing as traffic distribution evolves. A global counter approach uses a strongly consistent distributed store, such as Google Spanner or a CockroachDB cluster, to maintain a single global counter accessible across all regions, providing exact global enforcement at the cost of cross-region latency on every rate limit evaluation.

### Per-Endpoint Rate Limits

The current design applies a uniform limit across all endpoints for a given API key. Extending to per-endpoint limits requires adding the endpoint path or a normalised endpoint identifier as an additional component of the Redis key — for example, `apikey:<value>:endpoint:<name>:window:<id>` — and maintaining separate counters per endpoint. The algorithm and enforcement sequence remain identical; only the key construction and the limit configuration require modification.

### Tiered Rate Limits by Subscription Level

Supporting differentiated limits for free, paid, and enterprise API key tiers requires the Rate Limiter Service to look up the applicable limit for a given API key before performing the estimate calculation. This lookup can be served from a local in-memory cache within the reverse proxy — populated from a configuration service or database at startup and refreshed periodically — adding negligible latency to the enforcement path while supporting arbitrarily complex tier configurations.

### Adaptive Rate Limiting

An advanced extension would dynamically adjust rate limits based on observed system health — tightening limits during periods of high fleet load and relaxing them during normal operation. This requires the Rate Limiter Service to receive real-time system health signals and adjust the limit threshold used in the estimate comparison accordingly, without modifying the counter state or the algorithm itself.

## 19. Diagram

<img src="./distributed_rate_limiter_for_api_design.png" width="100%" height="100%">
