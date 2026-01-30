# PostgreSQL vs Cassandra vs DynamoDB vs MongoDB vs Couchbase — Complete Comparison

This guide provides a comprehensive comparison of PostgreSQL, Cassandra, DynamoDB, MongoDB, and Couchbase, covering data models, sharding, indexing, read/write performance, transactions, scaling, and practical use cases.

---

## 1. PostgreSQL

**Type:** Relational (SQL)  
**Strength:** ACID compliance, strong consistency, complex queries  
**Use Cases:** Financial apps, booking systems, transactional SaaS

**CAP Model:** CP (Consistency + Partition Tolerance)

**Sharding:** Manual (requires extensions like **Citus** or **pg_shard**). Read replicas can scale reads; writes scale moderately.

**Indexing:**
- B-Tree, GIN (full-text), GiST (geospatial), BRIN (large sequential datasets)
- Secondary indexes fully supported
- Fast reads; writes slightly slower due to index maintenance

**Read/Write:** Reads: Excellent | Writes: Moderate

**Transactions:** Full ACID, MVCC, row/table locks

**Summary:** Best for **read-heavy, transactional workloads** with complex queries.

---

## 2. Cassandra

**Type:** NoSQL, wide-column store  
**Strength:** High write throughput, availability, linear scalability  
**Use Cases:** Metrics collection, logs, IoT, social feeds

**CAP Model:** AP (Availability + Partition Tolerance) with eventual consistency

**Sharding:** Automatic via **partition key**; peer-to-peer; linear horizontal scaling

**Indexing:** Primary key essential; secondary indexes limited for large tables

**Read/Write:** Reads: Moderate | Writes: Excellent

**Transactions:** Lightweight transactions (LWT) via Paxos; lock-free

**Summary:** Ideal for **write-heavy, highly available workloads**.

---

## 3. DynamoDB

**Type:** NoSQL, key-value/document store  
**Strength:** Fully managed, serverless, low-latency, elastic  
**Use Cases:** Gaming backends, session stores, high-scale APIs

**CAP Model:** AP with eventual consistency by default

**Sharding:** Automatic, AWS-managed; partition key required; supports Global Tables

**Indexing:** Primary key (hash + optional sort), GSI, LSI

**Read/Write:** Excellent for both reads and writes

**Transactions:** Item-level and multi-item (up to 100 items), optimistic concurrency

**Summary:** Best for **high read/write scale workloads** with predictable access patterns.

---

## 4. MongoDB

**Type:** NoSQL, document store  
**Strength:** Flexible schema, horizontal scaling, cloud-managed options  
**Use Cases:** Content management, analytics, user profiles, serverless apps

**CAP Model:** AP with tunable consistency

**Sharding:** Automatic via **shard key**; supports replica sets

**Indexing:** Primary `_id`; secondary, compound, geospatial, text, TTL

**Read/Write:** Excellent for both reads and writes

**Transactions:** Multi-document ACID transactions supported; configurable write concern

**Summary:** Ideal for **flexible-schema, cloud-friendly applications**.

---

## 5. Couchbase

**Type:** NoSQL, document + key-value store  
**Strength:** High-performance, memory-first architecture, N1QL queries  
**Use Cases:** Caching, session stores, mobile apps, real-time analytics

**CAP Model:** AP, eventual consistency by default; strong consistency per document

**Sharding:** Automatic with replication; memory-first for high-speed operations

**Indexing:** Primary/secondary indexes, N1QL, full-text, geospatial

**Read/Write:** Excellent for both reads and writes

**Transactions:** ACID within bucket; multi-document transactions; optimistic concurrency

**Summary:** Best for **high-performance, distributed, flexible-schema applications**.

---

## Read/Write Suitability & Recommended Workloads

| Database   | Reads     | Writes    | Ideal Workload                     | Rationale                                                 |
|------------|-----------|-----------|------------------------------------|-----------------------------------------------------------|
| PostgreSQL | Excellent | Moderate  | Read-heavy, transactional          | Rich indexing, complex queries, ACID transactions         |
| Cassandra  | Moderate  | Excellent | Write-intensive, massive scale     | Append-only storage, peer-to-peer, high write throughput  |
| DynamoDB   | Excellent | Excellent | High-scale read/write, serverless  | Fully managed, automatic sharding, low-latency operations |
| MongoDB    | Excellent | Excellent | Flexible schema, cloud-native apps | Document-based, secondary indexes, scalable               |
| Couchbase  | Excellent | Excellent | High-performance distributed apps  | Memory-first, flexible schema, optimized for throughput   |

---

## CAP, Sharding & Core Characteristics

| Aspect              | PostgreSQL          | Cassandra        | DynamoDB             | MongoDB                   | Couchbase                     |
|---------------------|---------------------|------------------|----------------------|---------------------------|-------------------------------|
| CAP Model           | CP                  | AP (tunable)     | AP                   | AP (tunable)              | AP (per-document consistency) |
| Transactions        | ACID                | LWT / limited    | Item-level / limited | Multi-document ACID       | Bucket-level ACID             |
| Locking             | MVCC + locks        | None             | Optimistic           | Optimistic / replica sets | Optimistic / memory-first     |
| Sharding            | Manual / extensions | Automatic        | Automatic, managed   | Automatic                 | Automatic                     |
| Partition Key       | Optional            | Mandatory        | Mandatory            | Mandatory                 | Mandatory                     |
| Architecture        | Leader-based        | Peer-to-peer     | Multi-AZ managed     | Replica sets              | Clustered memory-first        |
| Global Distribution | Difficult           | Native           | Native               | Native                    | Native                        |
| Indexing            | Rich, flexible      | Primary-key only | Primary + GSI/LSI    | Primary + secondary       | Primary + secondary, N1QL     |
| Read Flexibility    | High                | Limited          | Limited              | Medium-High               | Medium-High                   |
| Write Scale         | Moderate            | Excellent        | Excellent            | Excellent                 | Excellent                     |
| Availability        | Medium              | Very High        | Extremely High       | High                      | Very High                     |

---

## When to Choose What

- **PostgreSQL:** Strong consistency, complex queries, transactional workloads.
- **Cassandra:** Massive write throughput, highly available distributed workloads.
- **DynamoDB:** Scalable, low-latency, fully managed applications with predictable access patterns.
- **MongoDB:** Flexible schema, cloud-friendly, high read/write workloads.
- **Couchbase:** High-performance, memory-first, distributed applications with flexible schema.

---

### Quick Reference

> **PostgreSQL = Correctness**  
> **Cassandra = Availability + Write Scale**  
> **DynamoDB = Scale with Simplicity**  
> **MongoDB = Flexible Schema + Cloud-Friendly**  
> **Couchbase = High Performance + Memory-First**
