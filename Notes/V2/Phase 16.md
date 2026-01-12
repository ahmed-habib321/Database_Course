# 🛡️ Phase 16 — Database Recovery Techniques
**Building Resilience into the Heart of Your Data System**

Databases are expected to be bulletproof. When a server loses power mid-transaction, when a disk fails, or when software crashes unexpectedly, your data must survive intact. Phase 16 explores the sophisticated mechanisms that allow databases to recover gracefully from virtually any failure scenario while maintaining data integrity.

---

## 🎯 The Recovery Promise

When disaster strikes, a robust recovery system guarantees three critical outcomes:

**Consistency Preservation**  
The database remains in a valid state. All constraints, relationships, and business rules continue to hold. The ACID properties that define reliable databases survive the crash.

**Committed Work is Sacred**  
If a transaction completed and the database confirmed the commit, that work must persist regardless of what happens afterward. Users must be able to trust that "saved" means saved.

**Incomplete Work Vanishes Cleanly**  
Conversely, if a transaction didn't reach commit before the failure, all its changes must be rolled back completely. No partial updates, no inconsistent states—it's as if the transaction never started.

---

## ⚠️ Understanding Failure Types

Different failures require different recovery strategies:

### Transaction Failure
**Cause**: Logic errors in application code, constraint violations, explicit rollback requests, or application crashes.

**Scope**: Affects a single transaction while the database system remains operational.

**Recovery**: The database management system handles this automatically using transaction logs, with no downtime required.

### System Crash
**Cause**: Power outages, operating system crashes, hardware panics, or DBMS software failures.

**Scope**: The entire database server goes down abruptly. Everything in volatile memory (RAM) is lost, but disk storage survives.

**Recovery**: Upon restart, the database replays logs to restore committed transactions and undo incomplete ones. This typically takes seconds to minutes.

### Disk Failure
**Cause**: Hard drive mechanical failure, SSD corruption, controller malfunction, or storage array issues.

**Scope**: Specific database files become unreadable or corrupted. Some data may be permanently lost from the failed disk.

**Recovery**: Requires restoring from backups and replaying transaction logs. If using RAID or replication, the system may continue operating with degraded performance.

### Media Failure (Catastrophic)
**Cause**: Fire, flood, ransomware, complete data center failure, or widespread corruption across all storage.

**Scope**: Total loss of the primary database and potentially local backups.

**Recovery**: Requires off-site backups and comprehensive disaster recovery procedures. Recovery time measured in hours or days.

---

## 📝 Deferred Update Recovery

The deferred update approach takes a cautious strategy: don't touch the actual database until you're absolutely certain the transaction will commit.

### How It Works

**During Transaction Execution**:
- All changes are held in memory buffers
- Every modification is logged to the transaction log
- The actual database pages on disk remain unchanged
- Memory tracks what *would* change if we commit

**At Commit Time**:
- Transaction log is flushed to disk (ensuring durability)
- Only then are the accumulated changes written to database pages
- Once database pages are updated, the commit is complete

**On Crash**:
- Any uncommitted transactions simply disappear—they never touched the database
- Committed transactions may need to be replayed if their changes were still in memory

### Advantages
**Simplified Rollback**: If a transaction aborts, just discard its memory buffers. No undo operations needed since the database was never modified.

**Clean Failures**: Crashes are simple to handle—uncommitted work automatically vanishes because it was never written.

**Consistency**: The database on disk always reflects a valid state, never showing partial transaction effects.

### Disadvantages
**Memory Pressure**: Large transactions must hold all their changes in RAM until commit, which can be prohibitive for bulk operations.

**Commit Latency**: At commit time, potentially large volumes of data must be written to disk, creating a performance spike.

**Scalability Issues**: Multiple large concurrent transactions competing for memory can cause resource exhaustion.

### Best Suited For
Small to medium transactions in systems where simplicity and correctness outweigh raw performance. Less common in modern high-performance databases.

---

## ⚡ Immediate Update Recovery

The immediate update approach prioritizes performance: write changes to the database as soon as they're made, even before commit. This requires more sophisticated recovery mechanisms.

### How It Works

**During Transaction Execution**:
- Changes are written directly to database pages (potentially still in memory buffers)
- Both "before" and "after" values are logged
- Undo log records the original values (for rollback)
- Redo log records the new values (for recovery)

**At Commit Time**:
- Transaction log is flushed to ensure durability
- Database pages may be flushed to disk opportunistically
- Commit happens quickly since most work was already done

**On Crash**:
- Redo log replays committed transactions whose changes might not have reached disk
- Undo log reverses uncommitted transactions that modified the database

### The Undo/Redo Protocol

**Undo Phase** (going backward):
```
For each uncommitted transaction found in the log:
    1. Start from the transaction's last log entry
    2. Work backward through its operations
    3. Restore original values from undo log
    4. Mark transaction as rolled back
```

**Redo Phase** (going forward):
```
For each committed transaction:
    1. Start from the transaction's first log entry
    2. Work forward through its operations
    3. Reapply new values from redo log
    4. Ensure all committed work persists
```

### Advantages
**Better Performance**: Commits are fast since changes are written incrementally rather than all at once.

**Memory Efficiency**: No need to buffer entire transactions—changes flow to disk continuously.

**Concurrency**: Multiple transactions can write simultaneously without waiting for commits.

### Disadvantages
**Complex Recovery**: Must carefully coordinate undo and redo operations during recovery.

**Log Overhead**: Every change requires logging both before and after images, consuming more log space.

**Crash Cleanup**: Recovery after crashes takes longer due to the two-phase undo/redo process.

### Best Suited For
High-performance transactional systems with many concurrent users. This is the approach used by most modern databases.

---

## 👥 Shadow Paging

Shadow paging takes a completely different approach inspired by copy-on-write filesystems: maintain two versions of the database simultaneously.

### The Concept

**The Page Table**:
The database maintains a page table (essentially a directory) that maps logical page numbers to physical disk locations.

**Two Versions**:
- **Current Page Table**: Points to pages currently being modified
- **Shadow Page Table**: Points to the last consistent database state

**How Updates Work**:
1. When a transaction modifies a page, create a copy of that page
2. Make changes to the copy (leaving the original unchanged)
3. Update entries in the current page table to point to modified pages
4. Shadow page table continues pointing to original pages

**At Commit**:
Simply swap pointers—make the current page table the new "official" version. This is an atomic operation.

**On Crash**:
Just discard the current page table and revert to the shadow version. No log replay needed.

### Advantages
**Extremely Simple Recovery**: Crashes are handled by a simple pointer swap. No complex log analysis or replay.

**Instant Rollback**: Aborting a transaction means discarding the modified pages and keeping shadows.

**Historical Snapshots**: Multiple shadow versions can provide point-in-time views.

### Disadvantages
**Storage Overhead**: Every modified page requires a duplicate, potentially doubling storage needs.

**Fragmentation**: Over time, related pages become scattered across the disk, hurting sequential access performance.

**Garbage Collection**: Must periodically reclaim old shadow pages no longer needed.

**Poor for Concurrent Writes**: Difficult to coordinate multiple transactions modifying the same logical area.

### Best Suited For
Specialized systems where simplicity and instant recovery outweigh storage efficiency. Rarely used in mainstream databases but influences modern copy-on-write storage systems.

---

## 🏆 ARIES: The Gold Standard

ARIES (Algorithm for Recovery and Isolation Exploiting Semantics) represents the culmination of decades of recovery algorithm research. It's the foundation for recovery in PostgreSQL, Oracle, IBM DB2, and many other enterprise databases.

### Core Principles

**Write-Ahead Logging (WAL)**  
Changes must be logged *before* they're written to the database. This iron-clad rule ensures recovery is always possible. The log sequence number (LSN) tracks ordering.

**Steal/No-Force Policy**:
- **Steal**: Uncommitted changes can be written to disk (improving performance)
- **No-Force**: Committed changes don't have to be written immediately (also improving performance)
- This flexibility creates complexity that ARIES elegantly handles

**Repeating History**  
During recovery, ARIES replays *everything* that happened, including actions from uncommitted transactions. This restores the database to its exact state at crash time before undoing incomplete work.

**Logical and Physical Logging**  
ARIES logs both low-level page modifications and high-level logical operations, enabling sophisticated optimizations while maintaining correctness.

### The Three-Phase Recovery Process

#### Phase 1: Analysis
**Goal**: Understand what was happening when the crash occurred.

**Actions**:
- Scan the transaction log forward from the last checkpoint
- Build a transaction table (which transactions were active)
- Build a dirty page table (which database pages had uncommitted changes)
- Determine the earliest log record that needs to be replayed

**Output**: Complete picture of the database state at crash time.

#### Phase 2: Redo
**Goal**: Restore the database to its exact state at the moment of crash.

**Actions**:
- Start from the earliest log record identified in Analysis
- Replay *every* logged operation, including those from uncommitted transactions
- Apply changes to database pages in memory
- This "repeats history" precisely

**Why redo uncommitted transactions?**  
Because under the steal policy, their changes might have been written to disk before the crash. We need a consistent starting point before undo.

#### Phase 3: Undo
**Goal**: Roll back all incomplete transactions identified during Analysis.

**Actions**:
- For each uncommitted transaction, process its log records backward
- Restore original values from undo records
- Generate compensation log records (CLRs) to ensure this undo work itself is logged
- Continue until all incomplete work is reversed

**Result**: Database is now in a consistent state containing only committed transactions.

### Advanced ARIES Features

**Checkpointing**  
Periodically, ARIES creates checkpoints—markers in the log indicating a consistent state. This limits how far back recovery must scan, dramatically reducing recovery time after crashes.

**Fuzzy Checkpoints**  
Unlike older systems that blocked all activity during checkpoints, ARIES supports fuzzy checkpoints that run concurrently with normal operations, avoiding downtime.

**Partial Rollbacks**  
ARIES can rollback to savepoints within a transaction rather than aborting entirely, supporting sophisticated error handling.

**Nested Top Actions**  
Certain operations (like index structure modifications) can be marked as atomic even within larger transactions, improving concurrency and recovery efficiency.

### Why ARIES Dominates

**Flexibility**: Supports diverse workloads from high-throughput OLTP to complex analytical queries.

**Performance**: The steal/no-force policy maximizes throughput by decoupling transaction commit from disk writes.

**Robustness**: Handles arbitrary crash points, even mid-checkpoint or during recovery itself.

**Extensibility**: The logical logging framework supports complex data types and operations beyond simple reads and writes.

---

## 💾 Backup and Disaster Recovery

Recovery algorithms handle crashes gracefully, but they assume the underlying storage survives. For catastrophic failures, backups are essential.

### Backup Strategies

**Full Backup**  
A complete copy of the entire database at a specific point in time.

- **Pros**: Simple to understand and restore from
- **Cons**: Time-consuming, storage-intensive, and resource-heavy during backup
- **Frequency**: Weekly or monthly for large databases

**Incremental Backup**  
Captures only data that changed since the *last backup* (of any type).

- **Pros**: Fast to create, minimal storage per backup
- **Cons**: Restoration requires the full backup plus all subsequent incrementals in sequence
- **Frequency**: Daily or even hourly

**Differential Backup**  
Captures all data changed since the last *full backup*.

- **Pros**: Faster restoration than incremental (only need full + latest differential)
- **Cons**: Each differential grows larger over time until the next full backup
- **Frequency**: Daily between full backups

### Example Backup Schedule
```
Sunday:    Full backup
Monday:    Incremental (changes since Sunday)
Tuesday:   Incremental (changes since Monday)
Wednesday: Incremental (changes since Tuesday)
...
Next Sunday: Full backup (cycle repeats)
```

To restore to Wednesday evening: Full backup + Monday incremental + Tuesday incremental + Wednesday incremental.

### Point-in-Time Recovery (PITR)

PITR allows restoring the database to any specific moment, not just backup times.

**How It Works**:
1. Restore from the most recent full backup
2. Apply any necessary incremental/differential backups
3. Replay transaction logs from that point forward to the desired timestamp
4. Stop replaying just before any unwanted changes (like accidental deletions)

**Use Cases**:
- **Accidental Deletion**: "Restore to 5 minutes before someone dropped the customer table"
- **Corruption Detection**: "Roll back to before data corruption was introduced"
- **Compliance**: "Show our database state during a specific transaction for audit purposes"

### The 3-2-1 Backup Rule

A best practice for comprehensive protection:
- **3** copies of your data (original + 2 backups)
- **2** different storage types (disk + tape, or local + cloud)
- **1** copy off-site (protecting against site-wide disasters)

### Recovery Time Objective (RTO) vs Recovery Point Objective (RPO)

**RTO**: How quickly must the database be operational after failure?  
Determines your recovery strategy—hot standby for minutes, backup restore for hours.

**RPO**: How much data loss is acceptable?  
Determines backup frequency—hourly backups mean up to an hour of lost transactions.

---

## 🌐 Recovery in Distributed Systems

When databases span multiple servers or data centers, recovery becomes significantly more complex.

### The Distributed Challenge

**Multiple Failure Domains**  
One node can crash while others continue operating. Or network partitions can isolate nodes from each other without any actual crashes.

**Consistency Across Nodes**  
A transaction might commit on some nodes but not others if a failure occurs mid-process. The distributed system must reach consensus about which transactions succeeded.

**Partial Failures**  
Unlike single-server databases where failure is total, distributed systems experience partial failures where some components work while others don't.

### Two-Phase Commit Recovery

When using two-phase commit for distributed transactions, failures during the protocol require special handling:

**Failure During Prepare Phase**:
- Coordinator crashes: Participants time out and abort
- Participant crashes: Coordinator aborts the transaction
- Recovery: Rollback all involved nodes

**Failure During Commit Phase**:
- Coordinator crashes after some commits: Must complete commits on remaining nodes when coordinator recovers
- Participant crashes: Must contact coordinator to learn transaction outcome

**The Uncertainty Period**:
If a participant votes "yes" but crashes before learning the final decision, it enters an uncertain state upon recovery. It must contact the coordinator to learn whether to commit or abort—blocking until communication is restored.

### Modern Distributed Recovery Approaches

**Consensus Protocols**  
Systems like Paxos and Raft provide reliable agreement about transaction outcomes even with node failures, replacing traditional two-phase commit.

**Replication for Resilience**  
Each transaction is replicated across multiple nodes. If one node fails, others maintain the authoritative record.

**Eventual Consistency Models**  
Some distributed systems relax immediate consistency requirements, allowing temporary divergence with guaranteed eventual convergence. Recovery becomes less about precise transaction outcomes and more about conflict resolution.

### Cross-Region Recovery

**Synchronous Replication**: Changes are written to multiple regions before acknowledging. Zero data loss but higher latency.

**Asynchronous Replication**: Primary site acknowledges first, replicates later. Better performance but potential data loss if primary fails.

**Active-Active**: Multiple regions accept writes simultaneously, with conflict resolution strategies. Maximum availability but complex recovery.

---

## 🎓 Key Takeaways

**Recovery is fundamental to database reliability**  
Without robust recovery mechanisms, databases would be unusable in production. Every transaction's value depends on knowing it will survive failures.

**Deferred vs. Immediate represents a core trade-off**  
Deferred update offers simplicity at the cost of memory and performance. Immediate update provides better throughput but requires sophisticated undo/redo mechanisms. Modern databases choose immediate update.

**Shadow paging is elegant but impractical**  
While conceptually beautiful—recovery via pointer swap—the storage overhead and fragmentation make it unsuitable for most real-world workloads. Its influence lives on in copy-on-write storage systems.

**ARIES is the modern standard**  
Its three-phase recovery (Analysis, Redo, Undo) combined with write-ahead logging and steal/no-force policies provides the optimal balance of performance and correctness. Understanding ARIES means understanding how production databases recover.

**Backups protect against storage failures**  
Recovery algorithms handle crashes when storage survives. Backups protect against the scenarios where storage itself fails. Both layers are essential.

**Distributed recovery introduces coordination challenges**  
Ensuring consistency across multiple nodes requires protocols like two-phase commit or consensus algorithms. Network partitions and partial failures make distributed recovery fundamentally harder than single-node recovery.

**The goal is invisible reliability**  
The best recovery systems are ones users never notice. Crashes happen, disks fail, power goes out—but from the user's perspective, the database is simply always available and always correct.