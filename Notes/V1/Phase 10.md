# 🚀 Phase 10 — Storage Engines & Database Architecture
Phase 10 is where we go under the hood of databases — how they physically store, organize, and access data. Understanding this is essential for performance tuning, storage planning, and choosing the right engine for your workload.


## 1️⃣ Storage Hierarchy
Data is stored in multiple levels, each with trade-offs:

| Level     | Type                   | Speed   | Cost      | Usage                     |
| --------- | ---------------------- | ------- | --------- | ------------------------- |
| Primary   | RAM / Cache            | Fastest | Expensive | Active data, buffer pools |
| Secondary | SSD / HDD              | Fast    | Medium    | Main database files       |
| Tertiary  | Tape / Cloud / Archive | Slowest | Cheap     | Backup, archival data     |

💡 Databases move frequently accessed pages to RAM for speed.

## 2️⃣ Storage Engines
Different engines have different features and performance trade-offs.

| Engine                                                 | Characteristics                                      | Use Case                                     |
| ------------------------------------------------------ | ---------------------------------------------------- | -------------------------------------------- |
| **InnoDB**                                             | ACID, row-level locking, MVCC, clustered primary key | OLTP, transactional workloads                |
| **MyISAM**                                             | Table-level locking, no foreign keys, fast reads     | Read-heavy workloads, small apps             |
| **RocksDB / LSM-Tree**                                 | Log-structured merge trees, write-optimized          | High write throughput, key-value stores      |
| **Columnar engines (ClickHouse, MariaDB ColumnStore)** | Columns stored separately                            | Analytics, OLAP, aggregation-heavy workloads |

💡 Rule: OLTP → InnoDB / LSM, OLAP → Columnar

## 3️⃣ B-Tree vs LSM-Tree
| Structure    | Reads                        | Writes                          | Notes                         |
| ------------ | ---------------------------- | ------------------------------- | ----------------------------- |
| **B-Tree**   | Fast for point/range queries | Slower for heavy writes         | Used by InnoDB, MySQL indexes |
| **LSM-Tree** | Slightly slower reads        | Optimized for sequential writes | RocksDB, LevelDB, Cassandra   |

4️⃣ Pages, Extents & Segments
- Page: smallest unit read/written to disk (e.g., 16KB)
- Extent: group of pages (e.g., 64 pages)
- Segment: large allocation for a table/index 
- Idea: DB reads/writes pages from disk into buffer pool.

## 5️⃣ File Structures
### Heap Files (Unordered)
- Data stored in no particular order 
- Pros: fast inserts 
- Cons: slow searches (full scan needed)

### Sorted / Ordered Files
- Data sorted by key 
- Pros: fast search (binary search), good for range queries 
- Cons: slower insert (must maintain order)

### Operations on Files
- Insert, delete, update 
- Search (binary / linear depending on order)
- Sequential scan for analytics

## 6️⃣ Buffer Pool & WAL
| Concept                   | Function                                                    |
| ------------------------- | ----------------------------------------------------------- |
| **Buffer Pool**           | Memory cache of frequently used pages for fast reads/writes |
| **Shared Memory**         | DB engine coordination, locks, metadata                     |
| **WAL (Write-Ahead Log)** | Durability: log changes **before** writing to disk          |

💡 WAL ensures ACID durability even if the system crashes mid-write.

## 7️⃣ Hashing
Used for fast lookups in memory or on disk.

#### Internal vs External Hashing
- Internal: All buckets fit in memory 
- External: Some buckets stored on disk
#### Collision Handling
- Chaining: Linked list of collided keys 
- Open Addressing: Probe next available slot 
#### Advanced Hashing
- Extendible Hashing: Dynamic directory grows as data grows 
- Linear / Dynamic Hashing: Gradual expansion to avoid rehashing all data

## 8️⃣ RAID & Disk I/O
Databases rely on RAID and storage strategies to improve speed & durability:

| RAID Level | Purpose                              |
| ---------- | ------------------------------------ |
| 0          | Striping → max speed, no redundancy  |
| 1          | Mirroring → max safety, read speed ↑ |
| 5 / 6      | Parity → balance of speed & safety   |

💡 Databases optimize disk I/O by reading/writing sequential pages, batching writes, and using WAL.

## 9️⃣ Persistent vs Transient Data
| Type           | Description                 | Examples                             |
| -------------- | --------------------------- | ------------------------------------ |
| **Persistent** | Survives crashes & shutdown | Table data, WAL logs, indexes        |
| **Transient**  | Exists only in memory       | Cache, buffer pool, temporary tables |

Rule: Keep critical data persistent, temp/working data in memory.