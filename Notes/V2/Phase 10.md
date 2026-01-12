# Database Storage Engines & Architecture - Complete Guide

Understanding how databases physically store and retrieve data is crucial for optimizing performance, planning capacity, and selecting the right technology for your needs. This guide breaks down the core concepts of database storage architecture.

---

## The Storage Pyramid: Where Data Lives

Databases don't store all data in one place. Instead, they use a hierarchy of storage types, each balancing speed against cost:

**Primary Storage (RAM/Cache)**  
This is your database's working memory. Frequently accessed data pages live here because RAM is thousands of times faster than disk. The buffer pool (explained later) manages this layer. It's expensive per gigabyte but essential for performance.

**Secondary Storage (SSDs and Hard Drives)**  
This is where your actual database files reside permanently. SSDs offer a good middle ground between speed and cost, while traditional hard drives provide cheaper bulk storage at the expense of slower access times.

**Tertiary Storage (Tape/Cloud Archives)**  
For long-term backups and data you rarely access, tertiary storage is cost-effective. Retrieving data takes much longer, but you're paying far less to store it.

**Key insight:** Databases constantly shuffle data between these layers, keeping hot data in fast storage and cold data in cheaper storage.

---

## Storage Engines: Different Tools for Different Jobs

A storage engine is the component that actually handles reading and writing data to disk. Different engines make different trade-offs.

### InnoDB (The OLTP Workhorse)
InnoDB is MySQL's default engine and designed for transactional workloads. It offers full ACID compliance, meaning your transactions are safe even if the system crashes. It uses row-level locking so multiple transactions can modify different rows simultaneously without blocking each other. InnoDB organizes data using a clustered index on the primary key, meaning rows are physically stored in primary key order for fast lookups.

**Best for:** E-commerce sites, banking systems, any application with lots of concurrent users making small transactions.

### MyISAM (The Simple Reader)
MyISAM is an older, simpler engine. It locks entire tables during writes, which means only one write can happen at a time. It doesn't support foreign keys or transactions. However, for read-heavy workloads with infrequent writes, it can be quite fast.

**Best for:** Legacy applications, small websites with mostly read traffic, situations where you don't need transactions.

### RocksDB and LSM-Tree Engines
These engines use a completely different architecture optimized for write-heavy workloads. Instead of updating data in place, they append new data to log files and periodically merge these logs. This makes writes extremely fast but can slow down reads slightly.

**Best for:** Time-series data, logging systems, applications that write far more often than they read.

### Columnar Engines (ClickHouse, MariaDB ColumnStore)
Instead of storing entire rows together, columnar engines store each column separately. This is perfect for analytics because queries typically only need a few columns from millions of rows. Reading just the columns you need is far faster than scanning entire rows.

**Best for:** Data warehouses, analytics dashboards, reporting systems, any OLAP workload.

---

## B-Trees vs LSM-Trees: The Fundamental Divide

Most databases use one of two core data structures:

### B-Trees (The Traditional Choice)
B-Trees organize data in a sorted tree structure where each node can have many children. Finding a specific record requires traversing from the root down to a leaf node. B-Trees excel at reads because finding data takes only a few disk accesses (typically 3-4 even for millions of records).

However, updates require finding the exact page, modifying it, and writing it back to disk. For write-heavy workloads, this becomes a bottleneck.

**Used by:** MySQL/InnoDB, PostgreSQL, most traditional relational databases.

### LSM-Trees (The Write-Optimized Alternative)
Log-Structured Merge Trees work differently. Writes go into an in-memory structure first, then get flushed to disk as sorted files. Reading requires checking multiple files, which can be slower. Periodically, the database merges smaller files into larger ones (called compaction).

The trade-off is clear: writes are blazing fast, reads are slightly slower, but the database does background work to merge files.

**Used by:** RocksDB, LevelDB, Cassandra, many NoSQL databases.

---

## Pages, Extents, and Segments: How Data Gets Organized

Databases don't read individual bytes from disk. They work with larger chunks:

**Pages** are the fundamental unit of I/O, typically 16KB in MySQL. When you query a single row, the database actually reads the entire 16KB page containing that row into memory. This seems wasteful, but disk drives are optimized for reading larger chunks at once.

**Extents** are groups of contiguous pages (often 64 pages, or 1MB). When a table grows, the database allocates an entire extent at once rather than individual pages. This reduces fragmentation and improves sequential read performance.

**Segments** represent large logical structures like an entire table or index. A segment might span many extents.

Understanding this hierarchy explains why sequential scans can be faster than you'd expect—reading pages that are physically adjacent on disk is very efficient.

---

## File Organization Strategies

How should a database arrange records within files?

### Heap Files (Unordered Storage)
Records are stored in no particular order, just appended wherever there's space. Inserting is trivial—just add to the end. But finding a specific record requires scanning the entire file.

**When to use:** Small tables, temporary data, situations where you'll typically scan everything anyway.

### Sorted Files (Ordered Storage)
Records are kept in sorted order by some key. Now you can use binary search to find records quickly, and range queries become efficient. The downside? Inserts and updates are expensive because you must maintain sort order, potentially moving many records around.

**When to use:** Read-heavy workloads, when range queries are common, when the data doesn't change frequently.

In practice, databases often combine approaches: heap files for the main data, sorted index files for fast lookups.

---

## The Buffer Pool: Your Database's Cache

The buffer pool is a region of RAM where the database caches frequently accessed pages. When you query data:

1. The database first checks if the page is already in the buffer pool
2. If yes (a cache hit), it reads from RAM—extremely fast
3. If no (a cache miss), it reads from disk and loads the page into the buffer pool

The buffer pool uses eviction policies (like LRU—Least Recently Used) to decide what to keep when it fills up. A well-tuned buffer pool can eliminate the vast majority of disk I/O for read-heavy workloads.

**Typical sizing:** 60-80% of available RAM on a dedicated database server.

---

## Write-Ahead Logging (WAL): Ensuring Durability

How does a database guarantee that committed transactions survive a crash? The answer is the Write-Ahead Log.

Here's the sequence:

1. You commit a transaction
2. **Before** modifying any data pages on disk, the database writes a description of the change to the WAL
3. The WAL is on disk and durable
4. Only then does the database update the actual data pages (which might happen later)
5. If the system crashes, the database replays the WAL on startup to recover committed transactions

This separation of logging from data modification is what makes ACID durability possible. The WAL is sequential writes (fast), while data pages might be scattered across disk (slower).

---

## Hashing: Fast Lookups for Specific Keys

While B-Trees excel at range queries, hash indexes are faster for exact-match lookups.

### How Hashing Works
A hash function converts a key into a bucket number. The database stores the record in that bucket. Looking up a key just means computing its hash and checking that bucket—theoretically O(1) time.

### The Collision Problem
Multiple keys might hash to the same bucket. Two solutions:

**Chaining:** Each bucket is a linked list. Collided keys are stored in the same list.  
**Open Addressing:** If a bucket is full, probe other buckets (linearly or using a second hash function) until you find an empty spot.

### Advanced Hashing Schemes
Static hashing allocates a fixed number of buckets upfront. As data grows, buckets overflow and performance degrades.

**Extendible Hashing** uses a directory that can grow. When buckets overflow, the directory doubles in size, and only specific buckets split—avoiding the need to rehash everything.

**Linear Hashing** grows incrementally, splitting one bucket at a time in a round-robin fashion.

These techniques let hash indexes grow gracefully as data scales.

---

## RAID: Protecting Your Data at the Hardware Level

RAID (Redundant Array of Independent Disks) combines multiple physical drives to improve performance, reliability, or both.

**RAID 0 (Striping):** Data is split across multiple drives. Reads and writes are parallelized for maximum speed, but if any drive fails, all data is lost. Don't use this for critical databases.

**RAID 1 (Mirroring):** Every write goes to two drives simultaneously. If one fails, the other has a complete copy. Reads can be faster (read from either drive), but you pay 100% storage overhead.

**RAID 5/6 (Parity):** Data is striped with parity information that allows reconstructing data if a drive fails. RAID 5 tolerates one failure, RAID 6 tolerates two. This offers a better balance of redundancy and capacity than RAID 1.

Most production databases use RAID 10 (mirrored stripes) or RAID 6 for critical data.

---

## Persistent vs Transient Data

Not all database data needs to survive a restart.

**Persistent Data** survives crashes and shutdowns:
- Table data
- Indexes
- Transaction logs (WAL)
- Configuration files

**Transient Data** exists only while the database is running:
- The buffer pool cache
- Temporary tables created during query execution
- Locks and latches held by active transactions

Understanding this distinction helps with capacity planning and recovery procedures. After a crash, persistent data is recovered from disk, while transient data is rebuilt as needed.

---

## Bringing It All Together

Modern databases combine these concepts into sophisticated systems:

- Data lives in a **storage hierarchy** (RAM → SSD → Archive)
- The **storage engine** (InnoDB, RocksDB, etc.) determines ACID properties and performance characteristics
- Data is organized into **pages** and accessed via **B-Trees** or **LSM-Trees**
- The **buffer pool** caches hot data in RAM
- The **WAL** ensures durability
- **Indexes** (B-Tree or hash) accelerate lookups
- **RAID** provides hardware-level redundancy

When designing a database system or tuning performance, you're making trade-offs along all these dimensions. Want faster writes? Consider LSM-Trees. Need bulletproof durability? Ensure WAL is on redundant storage. Queries slow? Check your buffer pool hit rate and index coverage.

Understanding these fundamentals transforms database administration from black magic into informed engineering decisions.