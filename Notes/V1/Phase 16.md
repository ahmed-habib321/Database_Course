🚀 Phase 16 — Database Recovery Techniques
From basic crash recovery to advanced algorithms like ARIES

Phase 16 is where we focus on making databases resilient to failures. This phase complements transactions and high availability, but dives deeper into how to recover from crashes, power outages, or catastrophic failures.

## 1️⃣ Recovery Concepts
Recovery ensures that after a failure, the database:
- Maintains consistency (ACID properties still hold)
- Preserves committed transactions 
- Rolls back uncommitted transactions

Types of Failures:
- Transaction failure → logic error, program crash 
- System crash → power outage, OS crash 
- Disk failure → hardware corruption 
- Media failure → catastrophic storage loss

## 2️⃣ Recovery Techniques Based on Deferred Update
- Idea: Changes are not written to the database until commit 
- Pros: Simple rollback, no partial updates on disk 
- Cons: Slower for large transactions, requires holding changes in memory 
- Workflow:
  - Log the transaction updates in memory 
  - On commit → write changes to disk

## 3️⃣ Recovery Techniques Based on Immediate Update
- Idea: Changes are written to the database immediately, even before commit 
- Requires Undo/Redo logs 
- Pros: Faster commits, lower memory usage 
- Cons: Must undo uncommitted changes after crash

Undo/Redo Workflow:
- Undo log: Rollback uncommitted transactions 
- Redo log: Replay committed transactions

## 4️⃣ Shadow Paging
- Idea: Maintain two copies of database pages: current and shadow 
- Changes are written to shadow pages; only switch pointer to shadow on commit 
- Pros: Simplifies recovery (no log replay needed)
- Cons: High overhead, inefficient for large databases

## 5️⃣ ARIES Recovery Algorithm
ARIES = Algorithm for Recovery and Isolation Exploiting Semantics

Key Features:

- Write-ahead logging (WAL)
- Repeating history during redo 
- Logging logical and physical operations 
- Supports partial rollbacks, checkpoints, and crash recovery

ARIES Steps:
1. Analysis phase: Determine which transactions were active at crash time 
2. Redo phase: Replay all logged changes to ensure committed transactions are applied 
3. Undo phase: Rollback all uncommitted transactions using undo logs

Used in: PostgreSQL, Oracle, DB2

## 6️⃣ Database Backup & Recovery from Catastrophic Failures
- Types of backups:
  - Full backups 
  - Incremental backups (changes since last backup)
  - Differential backups (changes since last full backup)
- Point-in-time recovery allows restoring DB to a specific timestamp using logs 
- Regular backups + WAL + recovery plan = resilience against media failures

## 7️⃣ Recovery in Multidatabase Systems
- Distributed databases require coordinated recovery across nodes 
- Challenges:
  - Maintaining consistency across nodes 
  - Handling partial failures 
  - Two-phase commit recovery (if a node crashes mid-commit)

## 🎯 Phase 16 Key Takeaways
1. Recovery ensures ACID properties survive crashes 
2. Deferred vs Immediate update → trade-offs between simplicity & performance 
3. Shadow paging avoids log replay but is costly 
4. ARIES is the modern standard for complex transactional recovery 
5. Backups + WAL enable disaster recovery 
6. Distributed recovery introduces coordination challenges