# PostgreSQL vs Cassandra vs DynamoDB vs MongoDB vs Couchbase — Complete Comparison

This document provides a detailed deep dive into PostgreSQL, Cassandra, DynamoDB, MongoDB, and Couchbase, covering their data model, sharding, indexing, read/write capabilities, transactions, and scaling.

---

## 1. PostgreSQL

### Overview
- **Type:** Relational, SQL
- **Strength:** ACID compliance, strong consistency, complex queries
- **Use Cases:** Financial apps, booking systems, transactional SaaS platforms

PostgreSQL is a **CP system** (Consistency + Partition Tolerance in CAP) emphasizing correctness over availability.

### Sharding
- Not natively sharded; requires extensions like **Citus** or **pg_shard**
- Horizontal scaling is manual; read replicas can scale reads
- Best for moderate write workloads

### Indexing
- Rich indexing options:
    - **B-Tree:** Default, equality/range queries
    - **GIN:** Full-text search
    - **GiST:** Geospatial queries
    - **BRIN:** Large sequential datasets
- Secondary indexes fully supported
- Reads are fast; writes slightly slower due to index updates

### Read/Write Capabilities
- **Reads:** Excellent
- **Writes:** Medium

### Transactions
- Full ACID support
- MVCC (Multi-Version Concurrency Control)
- Row/table locks supported
- Ideal for workflows requiring correctness

### Summary
PostgreSQL is best for **read-heavy, transactional workloads** with complex queries.

---

## 2. Cassandra

### Overview
- **Type:** NoSQL, wide-column store
- **Strength:** High write throughput, availability, linear scalability
- **Use Cases:** Metrics collection, logs, IoT, social feeds

Cassandra is an **AP system** (Availability + Partition Tolerance) with **eventual consistency**.

### Sharding
- Automatic sharding using **partition key**
- Peer-to-peer architecture; all nodes are equal
- Writes and reads can go to any node
- Linear scalability

### Indexing
- Primary key (partition + clustering key) is essential
- Secondary indexes exist but **not recommended for large tables**
- Best practice: design data for access patterns

### Read/Write Capabilities
- **Writes:** Excellent
- **Reads:** Medium (key-based fast, range scans slow)

### Transactions
- Lightweight transactions (LWT) with Paxos
- Mostly eventual consistency
- Lock-free

### Summary
Cassandra is ideal for **write-heavy, highly available workloads**.

---

## 3. DynamoDB

### Overview
- **Type:** NoSQL, key-value / document store
- **Strength:** Fully managed, serverless, low-latency, elastic
- **Use Cases:** Gaming backends, session stores, high-scale APIs

DynamoDB is an **AP system** with **eventual consistency by default**.

### Sharding
- Automatic sharding managed by AWS
- Partition key is mandatory; hot key handling is critical
- Scales seamlessly across regions with Global Tables

### Indexing
- **Primary key:** hash key + optional sort key
- **Secondary indexes:** GSI, LSI

### Read/Write Capabilities
- **Writes:** Excellent
- **Reads:** Excellent, eventually consistent by default

### Transactions
- Supports item-level and multi-item transactions (up to 100 items)
- Optimistic concurrency control
- Fully managed, no locks exposed

### Summary
DynamoDB is ideal for **high read + write scale** workloads with predictable access patterns.

---

## 4. MongoDB

### Overview
- **Type:** NoSQL, document store
- **Strength:** Flexible schema, easy horizontal scaling, cloud-managed options
- **Use Cases:** Content management, analytics, user profiles, serverless apps

MongoDB is an **AP system** with tunable consistency.

### Sharding
- Automatic sharding using **shard key**
- Supports replica sets for high availability
- Scales horizontally with minimal operational overhead

### Indexing
- Primary key: `_id`
- Supports secondary indexes on any field
- Supports compound, geospatial, text, and TTL indexes

### Read/Write Capabilities
- **Reads:** Excellent (key-based and indexed queries)
- **Writes:** Excellent with replica set acknowledgment settings

### Transactions
- Multi-document ACID transactions supported since MongoDB 4.0
- Lock-free replication with configurable write concerns

### Summary
MongoDB is ideal for **flexible schema, high read/write, cloud-friendly workloads**.

---

## 5. Couchbase

### Overview
- **Type:** NoSQL, document + key-value store
- **Strength:** High performance, memory-first architecture, N1QL query support
- **Use Cases:** Caching, session stores, mobile apps, real-time analytics

Couchbase is an **AP system** with eventual consistency by default, but supports strong consistency per document.

### Sharding
- Automatic sharding and replication
- Memory-first architecture allows high-speed operations
- Scales horizontally with minimal ops

### Indexing
- Supports primary and secondary indexes
- N1QL query engine allows SQL-like queries on JSON documents
- Full-text and geospatial indexes supported

### Read/Write Capabilities
- **Reads:** Excellent (memory-first, indexed queries)
- **Writes:** Excellent (optimized for high throughput)

### Transactions
- Supports ACID transactions within a bucket (multi-document transactions supported)
- Optimistic concurrency control

### Summary
Couchbase is ideal for **high-performance, flexible schema, distributed applications**.

---

## Comparison of Read/Write Suitability

| Database    | Reads      | Writes     | Best Workload                        | Reason                                                    |
|------------|-----------|-----------|-------------------------------------|-----------------------------------------------------------|
| PostgreSQL | Excellent | Medium    | Read-heavy, transactional           | Rich indexing, complex queries, ACID transactions        |
| Cassandra  | Medium    | Excellent | Write-heavy, massive scale          | Append-only storage, peer-to-peer, high write throughput |
| DynamoDB   | Excellent | Excellent | High read + write, cloud/serverless | Fully managed, automatic sharding, low-latency operations |
| MongoDB    | Excellent | Excellent | Flexible schema, high-scale apps    | Document-based, secondary indexes, cloud-friendly         |
| Couchbase  | Excellent | Excellent | High-performance distributed apps   | Memory-first, flexible schema, optimized for throughput   |

---

## CAP Theorem Mapping (Practical View)

| Database   | Chooses      | Sacrifices                   |
|------------|--------------|------------------------------|
| PostgreSQL | Consistency  | Availability during failures |
| Cassandra  | Availability | Immediate consistency        |
| DynamoDB   | Availability | Strong consistency (optional)|
| MongoDB    | Availability | Immediate consistency (tunable) |
| Couchbase  | Availability | Strong consistency at scale (per doc) |

---

## Sharding & Distribution

| Aspect              | PostgreSQL          | Cassandra    | DynamoDB                | MongoDB            | Couchbase          |
|---------------------|-------------------|--------------|------------------------|------------------|------------------|
| Sharding Model      | Manual / extensions | Automatic   | Automatic (AWS-managed)| Automatic         | Automatic         |
| Partition Key       | Optional           | Mandatory   | Mandatory              | Mandatory         | Mandatory         |
| Architecture        | Leader-based       | Peer-to-peer| Multi-AZ managed       | Replica sets      | Clustered memory-first |
| Global Distribution | Difficult          | Native      | Native                 | Native            | Native            |

---

## When to Choose What (Interview Gold)

### Choose **PostgreSQL** when:
- Strong consistency and correctness are critical
- Complex queries and transactions are needed
- Use cases: fintech, inventory, SaaS backends

### Choose **Cassandra** when:
- Massive write throughput is required
- High availability is non-negotiable
- Use cases: logs, metrics, IoT, event ingestion

### Choose **DynamoDB** when:
- High read + write scale with low latency
- Minimal operational overhead
- Use cases: serverless apps, user profiles, gaming backends

### Choose **MongoDB** when:
- Flexible schema is needed
- High read/write with cloud-managed deployment
- Use cases: content management, analytics, serverless apps

### Choose **Couchbase** when:
- High-performance, memory-first operations required
- Flexible schema with distributed architecture
- Use cases: caching, mobile apps, real-time analytics

---

### Final Rule of Thumb
> **PostgreSQL = Correctness**  
> **Cassandra = Availability + Write Scale**  
> **DynamoDB = Scale with Simplicity**  
> **MongoDB = Flexible Schema + Cloud-Friendly**  
> **Couchbase = High Performance + Memory-First**  
