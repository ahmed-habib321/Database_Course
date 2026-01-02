# 🚀 Phase 9 — Transactions, Concurrency & Locking

Phase 9 is where databases stop being “data storage” and become real-world systems that protect money, orders, messages, flights, medical records, etc.  
This is the foundation of banking systems, booking systems, fintech, ERP, and critical applications.

We focus on:  
➡️ Transactions  
➡️ Concurrency (multiple users at once)  
➡️ Locking (protect shared data)

## 🧱 1️⃣ Transaction Fundamentals
A transaction is a unit of work that must be executed fully or not at all.

Example transaction in banking:

- T1: transfer 100$ from Account A → B
```postgresql
UPDATE accounts SET balance = balance - 100 WHERE id = 'A';
UPDATE accounts SET balance = balance + 100 WHERE id = 'B';
```
A crash can happen between updates — so transaction control is required.

### 🚦 Single User vs Multi-User Systems
| System      | Use Case               | Notes                         |
| ----------- | ---------------------- | ----------------------------- |
| Single-user | Local offline software | No concurrency issues         |
| Multi-user  | Databases, online apps | Must protect data consistency |

### 🎯 ACID Properties
| Property        | Meaning                                  | Ensures                     |
| --------------- | ---------------------------------------- | --------------------------- |
| **Atomicity**   | All or nothing                           | No partial changes          |
| **Consistency** | DB moves from one valid state to another | Rules are always respected  |
| **Isolation**   | Transactions don’t interfere             | Each sees a consistent view |
| **Durability**  | Once committed, stays permanent          | Survives crashes/power loss |

### 📌 Transaction States
```postgresql
Active → Partially Committed → Committed → Terminated
                ↘
                Aborted (Rollback)
```
Commands:
```postgresql
BEGIN;
COMMIT;
ROLLBACK;
SAVEPOINT S1;
ROLLBACK TO S1;
```
### 💾 System Log (Undo/Redo Logs)
The log stores history for recovery after failures.

| Log Type | Purpose                               |
| -------- | ------------------------------------- |
| Undo Log | Reverse uncommitted changes           |
| Redo Log | Reapply committed changes after crash |

## 🌐 2️⃣ Concurrency Control & Isolation
When two transactions run at the same time, data problems occur.

### ❌ Common Problems
| Problem               | Example                              | Effect                         |
| --------------------- | ------------------------------------ | ------------------------------ |
| **Dirty Read**        | Read uncommitted data                | Wrong / ghost values           |
| **Unrepeatable Read** | Same query returns different results | Instability                    |
| **Phantom Read**      | New rows appear during query         | Aggregates change unexpectedly |
| **Incorrect Summary** | Aggregates change mid-operation      | Reports become inaccurate      |

### 🧱 SQL Isolation Levels
| Level                | Dirty Read   | Unrepeatable Read | Phantom      |
| -------------------- | ------------ | ----------------- | ------------ |
| **Read Uncommitted** | ❌ Allowed    | ❌ Allowed         | ❌ Allowed    |
| **Read Committed**   | ✔️ Prevented | ❌ Allowed         | ❌ Allowed    |
| **Repeatable Read**  | ✔️ Prevented | ✔️ Prevented      | ❌ Allowed    |
| **Serializable**     | ✔️ Prevented | ✔️ Prevented      | ✔️ Prevented |

Higher isolation = safer but slower.

Command:
```postgresql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```
📌 MVCC (Multi-Version Concurrency Control)
Instead of locking, the DB keeps multiple versions of rows.  
✔️ Readers don’t block writers  
✔️ Writers don’t block readers 

Used in:
- PostgreSQL 
- MySQL InnoDB 
- Oracle (variant)

Uses undo logs to show past row versions to readers.

## 🔐 3️⃣ Locking Concepts
Locks = protect shared data from conflicts

### Lock Types
| Lock              | Purpose   | Example          |
| ----------------- | --------- | ---------------- |
| **Shared (S)**    | Read-only | `SELECT`         |
| **Exclusive (X)** | Write     | `UPDATE, DELETE` |

Rules:
- Many S locks allowed together 
- Only 1 X lock at a time 
- X lock blocks S, S doesn’t block S

### Intent Locks
Used in hierarchical locking (database → table → page → row)

| Lock Mode | Meaning                            |
| --------- | ---------------------------------- |
| **IS**    | Intend to get shared               |
| **IX**    | Intend to write                    |
| **SIX**   | Read whole table, write a few rows |

Prevent lock conflicts by pre-declaring purpose.

### Gap/Next-Key Locks
Protect the space between rows — prevents phantom inserts.

Used in Repeatable Read / Serializable isolation.

Example: `WHERE id BETWEEN 10 AND 20;  `
→ next-key lock stops inserts in that range.

### ⚙️ Lock Management
A lock manager in DBMS handles:
- granting locks 
- detecting conflicts 
- deadlock resolution
### Lock Conversion
- Upgrade → from S to X (read → write)
- Downgrade → X to S

📐 Granularity
Lock levels:
```postgresql
 Database
    │
  Table
    │
Page (block)
    │
   Row
    │
Field (rare)
```
Tradeoff:
- Fine granularity → concurrency ↑ but overhead ↑
- Coarse granularity → fast but many conflicts

## 🔁 4️⃣ Locking Protocols & Deadlocks
### ✌️ Two-Phase Locking (2PL)
Guarantees serializability

Phases:
```postgresql
Growing Phase: Acquire locks
Shrinking Phase: Release locks
```
No new lock after one is released.

### 💀 Deadlocks
Two transactions waiting on each other forever.

Example:
```postgresql
T1 has A, wants B
T2 has B, wants A
```
Solutions:
- Timeout 
- Deadlock detection graph 
- Force rollback of one transaction

### 🌧️ Starvation
A transaction never gets CPU/lock time → always waiting.

Fix: priority management & fairness.

## ⏳ 5️⃣ Timestamp-Based Concurrency
Each transaction gets a timestamp on start.

| Rule                                   | Behavior                      |
| -------------------------------------- | ----------------------------- |
| Old transaction wins                   | Newer one aborts              |
| Used in optimistic concurrency control | Best for low conflict systems |

### 🕒 Timestamp Ordering Protocols
| Type                   | Behavior                                     |
| ---------------------- | -------------------------------------------- |
| **Basic**              | Reject conflicting operations immediately    |
| **Strict**             | Avoid cascading aborts (undo fully isolated) |
| **Thomas' Write Rule** | Ignore outdated writes                       |

📌 Thomas’ Write Rule prevents unnecessary aborts when writes are obsolete.

### 🧠 Validation (Optimistic CC)
Assume conflicts are rare.

Steps:
1. Read Phase — no locks
2. Validation — check for conflicts
3. Write Phase — commit if no conflicts

Works well in:  
✔️ read-heavy workloads  
✔️ analytics systems

## 📊 6️⃣ Schedules & Serializability
Schedules = order of operations from multiple transactions combined.

### 📌 Conflict Rules
Operations conflict if:
- Same data 
- One is write

### 📌 Serializability
A schedule is serializable if its outcome is equal to some serial execution.

| Type                         | Meaning                              |
| ---------------------------- | ------------------------------------ |
| **Conflict Serializability** | reorder operations by conflict rules |
| **View Serializability**     | outcome matches serial               |

### 📌 Recoverability
| Property        | Meaning                                               |
| --------------- | ----------------------------------------------------- |
| **Recoverable** | Commit only after the transaction that wrote the data |
| **Cascadeless** | No read of uncommitted data                           |
| **Strict**      | No read/write until previous commit                   |

Helpful to avoid cascaded rollbacks.