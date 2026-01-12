# 🚀 Phase 14 — High Availability & Replication
Failover, scaling, distributed systems

Phase 14 is where we tackle resiliency, uptime, and scalability—how databases survive hardware failures, handle huge workloads, and serve users around the globe. This is mission-critical for production systems.

## 1️⃣ Master-Slave Replication
- Master (Primary) → Handles writes 
- Slave (Replica) → Handles reads 
- Replication: Data from master is asynchronously or synchronously copied to slaves

### Pros
- Read scalability (slaves handle SELECTs)
- Backup & disaster recovery

### Cons
- Writes still bottleneck at master 
- Potential lag between master & slave

Example MySQL replication:
```postgresql
-- On master
CHANGE MASTER TO MASTER_HOST='master_host', MASTER_USER='repl', MASTER_PASSWORD='pwd';

-- On slave
START SLAVE;
```
## 2️⃣ Multi-Master Replication
- Multiple nodes can accept writes simultaneously 
- Replicate changes to all other nodes 
- Used in geographically distributed systems

### Pros
- High write availability 
- No single point of failure
### Cons
- Conflict resolution required (last-write-wins, custom logic)
- Complex management

Use Case: Global SaaS apps with users in multiple regions

## 3️⃣ Sharding
- Horizontal partitioning → data split across multiple servers 
- Each shard stores a subset of data (e.g., users 1–100k on shard1, 100k–200k on shard2)

### Pros
- Linear scalability for writes & storage 
- Reduces load on individual servers

### Cons
- Cross-shard queries can be complex 
- Rebalancing shards is tricky

### Sharding Strategy:
- By user ID, region, or hash of primary key

## 4️⃣ Read/Write Splitting
- Sends writes to primary 
- Sends reads to replicas 
- Often combined with load balancers

### Benefit:
- Improves read-heavy workloads 
- Keeps writes consistent via replication

## 5️⃣ Heartbeat & Failover Systems
- Heartbeat → monitors health of primary/master node 
- Automatic failover → if primary fails, a replica is promoted

Example Tools:
- MySQL Group Replication 
- PostgreSQL Patroni 
- Pacemaker + Corosync

## 6️⃣ Distributed SQL
Modern SQL systems built for horizontal scaling:

| System          | Features                                                       | Notes                                    |
| --------------- | -------------------------------------------------------------- | ---------------------------------------- |
| **CockroachDB** | SQL interface, ACID, multi-region, automatic replication       | Compatible with Postgres                 |
| **YugabyteDB**  | PostgreSQL compatible, global distribution, strong consistency | Combines NoSQL scaling with SQL features |

### Advantages
- Fault-tolerant 
- Multi-region reads & writes 
- Automatic sharding & replication 
- Strong consistency (ACID)

## 7️⃣ Summary — Key Concepts
| Concept              | What It Solves                          |
| -------------------- | --------------------------------------- |
| Master-Slave         | Read scaling & backups                  |
| Multi-Master         | Write availability & failover           |
| Sharding             | Horizontal scaling for storage & writes |
| Read/Write Splitting | Optimized workload distribution         |
| Heartbeat & Failover | Automatic recovery & uptime             |
| Distributed SQL      | Global scale + SQL ACID consistency     |

## 🎯 Phase 14 Takeaways
1. HA = minimize downtime, maximize availability 
2. Replication ensures redundancy & read scaling 
3. Sharding scales writes & storage horizontally 
4. Distributed SQL combines global scale + ACID transactions 
5. Proper failover planning is critical for production-grade systems