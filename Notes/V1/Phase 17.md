# 🚀 Phase 17 — Distributed Databases & Client-Server Architectures
Scaling across multiple nodes while maintaining consistency and performance

Phase 17 is where we scale databases beyond a single machine. This phase builds on transactions, recovery, and high availability, introducing distribution, coordination, and multi-tier architectures. It’s essential for modern enterprise and cloud-scale systems.

## 1️⃣ Distributed Database Concepts
A distributed database (DDB) is a single logical database stored across multiple physical locations, connected via a network.

### Advantages:
- Improved availability & fault tolerance 
- Load distribution & parallel processing 
- Data localization for regional access

### Challenges:
- Maintaining consistency across nodes 
- Coordinating transactions & recovery 
- Handling network failures

## 2️⃣ Data Fragmentation, Replication & Allocation Techniques
### a) Fragmentation
- Splitting a table into smaller pieces (fragments)
- Types:
  - Horizontal Fragmentation: rows divided by criteria (e.g., region)
  - Vertical Fragmentation: columns divided across fragments 
  - Hybrid Fragmentation: combination of both

### b) Replication
- Copying fragments to multiple nodes for redundancy & performance 
- Synchronous → all replicas updated at once (strong consistency)
- Asynchronous → replicas updated later (eventual consistency)

### c) Allocation
- Determines where to store fragments 
- Goal: minimize communication, improve local access

## 3️⃣ Types of Distributed Database Systems
| Type          | Description                                       |
| ------------- | ------------------------------------------------- |
| Homogeneous   | All nodes run same DBMS, schema identical         |
| Heterogeneous | Nodes may use different DBMS or schemas           |
| Federated     | Multiple autonomous DBs appear as a single system |
| Client-Server | Users access DB via dedicated server(s)           |

## 4️⃣ Query Processing in Distributed Databases
- Queries can involve multiple nodes → need distributed query plan 
- Steps:
  - Parse & validate query 
  - Decompose query into subqueries for each site 
  - Optimize subqueries for network & CPU cost 
  - Execute subqueries, combine results

Optimizations:
- Push selections/projections to nodes 
- Minimize data transfer 
- Use indexes at local sites

## 5️⃣ Concurrency Control & Recovery in Distributed DBs
- Transactions span multiple nodes → distributed ACID 
- Two-phase commit (2PC) ensures atomicity across nodes 
- Distributed locks prevent conflicts 
- Recovery may require coordinated rollback if a node fails

Example:
```postgresql
T1 updates node A & node B
If node B fails → rollback changes on node A
```
## 6️⃣ 3-Tier Client-Server Architecture
| Tier                   | Function                            |
| ---------------------- | ----------------------------------- |
| **Presentation Layer** | UI / Web / Mobile app               |
| **Application Layer**  | Business logic, API servers         |
| **Database Layer**     | Data storage, queries, transactions |

Benefits:
- Separation of concerns 
- Scalable independently 
- Supports distributed systems

## 7️⃣ Distributed Databases in Oracle
- Oracle supports fragmentation, replication, and distributed transactions 
- Key Features:
  - Database links for cross-node queries 
  - Transparent Distributed Transactions using 2PC 
  - Replication & materialized views for reporting & redundancy

## 🎯 Phase 17 Key Takeaways
1. Distributed DB = logical database across multiple physical nodes 
2. Fragmentation, replication, allocation → key for performance & availability 
3. Query processing requires optimizing across nodes 
4. Distributed concurrency & recovery = multi-node ACID 
5. 3-Tier architecture separates UI, logic, and storage 
6. Oracle & modern DBMS provide built-in support for distributed transactions