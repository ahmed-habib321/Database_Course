# Database High Availability & Replication
*Building resilient, scalable database systems that never go down*

## What is High Availability?

High availability (HA) means designing systems that stay operational even when components fail. For databases, this involves ensuring your data remains accessible, consistent, and performant under all conditions—hardware failures, network issues, traffic spikes, or regional outages.

The core principles are:
- **Redundancy** - Multiple copies of your data across different servers
- **Fault tolerance** - Automatic recovery when something breaks
- **Scalability** - Handling growth in users, data, and transactions
- **Geographic distribution** - Serving users quickly regardless of location

---

## Replication Strategies

### Primary-Replica Architecture (Master-Slave)

This is the foundation of most HA setups. One database server (the primary) handles all write operations, while one or more replica servers receive copies of that data and handle read operations.

**How it works:**
1. Applications send INSERT, UPDATE, DELETE commands to the primary
2. The primary executes these writes and logs the changes
3. Replicas continuously pull these changes and apply them to their own copies
4. Applications send SELECT queries to replicas, distributing the read load

**Types of replication:**
- **Asynchronous** - Primary doesn't wait for replicas to confirm they received data. Faster writes, but replicas might lag behind slightly.
- **Synchronous** - Primary waits for at least one replica to confirm before completing the write. Slower but guarantees replicas are current.

**When to use this:**
- Read-heavy applications (social media feeds, content sites, dashboards)
- You need backup copies for disaster recovery
- You want to offload reporting queries from your production database

**Limitations:**
- All writes still go through one server, which can become a bottleneck
- Replicas might show slightly outdated data (replication lag)
- If the primary fails, you need a plan to promote a replica

**Setting up replication in PostgreSQL:**
```sql
-- On primary server: configure to allow replication
ALTER SYSTEM SET wal_level = replica;
ALTER SYSTEM SET max_wal_senders = 5;

-- Create replication user
CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'secure_pass';

-- On replica server: point to primary
primary_conninfo = 'host=primary.db.com port=5432 user=replicator password=secure_pass'
```

---

### Multi-Primary Replication (Multi-Master)

Instead of one primary, multiple database servers can all accept writes simultaneously. Each server replicates its changes to all other servers in the cluster.

**How it works:**
1. User in New York writes to the US database server
2. User in London writes to the EU database server (at the same time)
3. Both changes propagate to all servers
4. Each server merges incoming changes with its own data

**Conflict resolution:**
When two servers modify the same record simultaneously, you need rules:
- **Last-write-wins** - Timestamp determines which change survives (simple but can lose data)
- **Application-defined** - Custom business logic decides (e.g., "always prefer higher values")
- **Conflict detection** - Flag conflicts for manual review

**When to use this:**
- Global applications where users are geographically distributed
- You need write availability even if servers go offline
- No single server can handle all write traffic

**Challenges:**
- Complex to implement and manage correctly
- Requires careful thinking about how conflicts are resolved
- Network partitions can create divergent data states

**Example use case:**
A collaborative document editor where users in different continents edit simultaneously. Each region has a local database accepting writes instantly, with changes syncing globally in the background.

---

## Scaling Strategies

### Horizontal Partitioning (Sharding)

Instead of replicating the same data across servers, you split different portions of data across multiple servers. Each server (shard) is responsible for a subset of your total data.

**Common sharding strategies:**

**1. Range-based sharding**
```
Shard 1: User IDs 1 - 1,000,000
Shard 2: User IDs 1,000,001 - 2,000,000
Shard 3: User IDs 2,000,001 - 3,000,000
```

**2. Hash-based sharding**
```
shard = hash(user_id) % number_of_shards
```
This distributes data more evenly than ranges.

**3. Geographic sharding**
```
Shard US: All users in North America
Shard EU: All users in Europe
Shard APAC: All users in Asia-Pacific
```

**Benefits:**
- Each shard handles a fraction of total writes (linear scalability)
- Storage capacity grows by adding more shards
- Queries within a shard are fast since data sets are smaller

**Drawbacks:**
- Queries that need data from multiple shards become complex
- Joining data across shards requires application-level logic
- Rebalancing data when adding/removing shards is complicated
- No easy way to do transactions across shards

**Implementation considerations:**
```sql
-- Application logic determines which shard to query
user_id = 150000
shard_number = user_id // 100000  -- Result: 1

-- Query goes to shard 1
SELECT * FROM users WHERE user_id = 150000;
```

**When to use sharding:**
- Your data set is too large for a single server
- Write traffic exceeds what one server can handle
- You can partition data logically (by user, tenant, region)

---

### Read/Write Splitting

A pattern that routes different types of queries to different servers based on whether they modify data.

**Architecture:**
```
Application
    ↓
Load Balancer / Proxy
    ↓
    ├─→ Primary (writes) ← INSERT, UPDATE, DELETE
    ↓
    ├─→ Replica 1 (reads) ← SELECT
    ├─→ Replica 2 (reads) ← SELECT
    └─→ Replica 3 (reads) ← SELECT
```

**Implementation approaches:**

**1. Application-level routing**
```python
# In your application code
if query.is_write():
    connection = primary_db_pool.get_connection()
else:
    connection = replica_db_pool.get_connection()
```

**2. Middleware/Proxy routing**
Use tools like ProxySQL, PgBouncer, or HAProxy that inspect queries and route them automatically.

**Considerations:**
- **Replication lag** - After writing to primary, immediately reading from replica might show old data. Either read from primary after writes, or use synchronous replication.
- **Sticky sessions** - Sometimes you want a user's reads to go to primary temporarily after they make a write.

---

## Automatic Failover

When your primary database crashes, you need another server to take over instantly without manual intervention.

### Heartbeat Monitoring

A separate system continuously checks if the primary is healthy:

```
Monitor → ping primary every 2 seconds
        → if 3 consecutive pings fail (6 seconds)
        → trigger failover process
```

### Failover Process

1. **Detection** - Monitor confirms primary is down (not just a temporary network hiccup)
2. **Election** - System chooses which replica to promote (usually the one with most recent data)
3. **Promotion** - Chosen replica is reconfigured to accept writes
4. **Redirection** - Applications are pointed to the new primary
5. **Recovery** - Old primary (when it comes back) joins as a replica

**Tools that handle failover:**

**PostgreSQL:**
- **Patroni** - Uses etcd or Consul for distributed consensus, handles automatic failover
- **repmgr** - Monitors replication and provides failover capabilities
- **Stolon** - Cloud-native PostgreSQL HA

**MySQL:**
- **MySQL Group Replication** - Built-in multi-primary replication with automatic failover
- **Orchestrator** - Topology management and failover
- **MHA (Master High Availability)** - Automated master failover

**Split-brain prevention:**
A critical concern is ensuring only one server thinks it's the primary. Using distributed consensus systems (etcd, ZooKeeper, Consul) prevents this.

---

## Distributed SQL Databases

Modern databases built from the ground up for global distribution, combining SQL's familiar interface with NoSQL's horizontal scalability.

### Key Features

**Automatic sharding** - Data is partitioned across nodes without manual configuration

**Multi-region replication** - Data copied across geographic regions with tunable consistency

**Strong consistency** - ACID transactions even across distributed nodes

**Fault tolerance** - Survives node, datacenter, or region failures

### Popular Systems

**CockroachDB**
- PostgreSQL wire-compatible (use existing Postgres tools/drivers)
- Survives server, rack, or datacenter failures
- Automatic rebalancing when nodes are added/removed
- Geo-partitioning for data locality compliance (GDPR, etc.)

```sql
-- Specify data locality
ALTER DATABASE app CONFIGURE ZONE USING 
    num_replicas = 3,
    constraints = '{"+region=us-east": 1, "+region=us-west": 1, "+region=eu-west": 1}';
```

**YugabyteDB**
- PostgreSQL-compatible API
- Based on Google Spanner architecture
- Supports both SQL and Cassandra-compatible NoSQL APIs
- Linear scalability by adding nodes

**Google Cloud Spanner**
- Globally distributed, strongly consistent
- Automatic horizontal scaling
- 99.999% availability SLA

**When to use distributed SQL:**
- You need global presence with low latency everywhere
- Strong consistency is non-negotiable (financial transactions)
- You want SQL but need to scale beyond single-server limits
- Automatic operational management is worth the higher cost

---

## Putting It All Together

### Small Application (< 10,000 users)
- Single database server
- Regular backups
- Vertical scaling (bigger server) when needed

### Medium Application (10,000 - 1M users)
- Primary-replica architecture
- Read/write splitting
- Automated failover
- Sharding if data grows large

### Large Application (> 1M users)
- Multiple replicas across regions
- Sharding across multiple dimensions
- Distributed SQL for global consistency
- CDN for static content
- Caching layer (Redis/Memcached) to reduce database load

---

## Decision Framework

**Choose Primary-Replica when:**
- Reads significantly outnumber writes (10:1 or more)
- You need disaster recovery capabilities
- Budget is constrained

**Choose Multi-Primary when:**
- Users are globally distributed and need local write performance
- You can tolerate or handle eventual consistency
- High write availability is critical

**Choose Sharding when:**
- Single database can't store all your data
- Write volume exceeds single server capacity
- Data naturally partitions (multi-tenant, geographic)

**Choose Distributed SQL when:**
- You need global scale with strong consistency
- Operational complexity must be minimized
- Budget allows for premium solutions
- Regulatory requirements demand data locality

---

## Key Takeaways

1. **Replication provides redundancy** - Copies of data protect against hardware failure and enable read scaling

2. **No single approach fits all scenarios** - Mix strategies based on your specific requirements (data size, traffic patterns, consistency needs)

3. **Consistency vs. availability trade-offs** - You generally can't have perfect consistency, perfect availability, and network partition tolerance simultaneously (CAP theorem)

4. **Test your failover** - Regularly simulate failures to ensure your HA setup works when disaster strikes

5. **Monitor replication lag** - Know how far behind your replicas are, especially for applications requiring recent data

6. **Plan for growth** - Migrating from one architecture to another under load is painful; design for 10x your current scale

High availability isn't a product you buy, it's a discipline you practice. Start simple, add complexity only when needed, and always test your assumptions about how systems will fail.