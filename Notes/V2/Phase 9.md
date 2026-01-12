# Phase 9: Transactions, Concurrency & Locking

## Why This Matters

Imagine you're transferring $500 from your savings to checking. The system deducts $500 from savings—then crashes before adding it to checking. Your money just vanished. Or picture two people booking the last seat on a flight at the exact same moment. Who gets it? Do both get confirmation emails?

These aren't hypothetical problems—they're real challenges that every banking system, e-commerce platform, booking service, and healthcare application must solve. Phase 9 teaches you how databases guarantee data integrity when things go wrong and when multiple users compete for the same resources.

---

## Part 1: Understanding Transactions

### What Is a Transaction?

A **transaction** is a bundle of database operations that must succeed or fail together—no middle ground. Think of it as an all-or-nothing deal.

**Real-world example: Transferring money**

```sql
BEGIN;
UPDATE accounts SET balance = balance - 100 WHERE id = 'Alice';
UPDATE accounts SET balance = balance + 100 WHERE id = 'Bob';
COMMIT;
```

If the system crashes after the first UPDATE but before the second, the transaction gets rolled back—Alice keeps her $100, and Bob gets nothing. The alternative (losing Alice's money or duplicating it) would be catastrophic.

### Single-User vs Multi-User Systems

**Single-user systems** (like a local desktop app with a personal database) don't face concurrency issues—only one person uses them at a time.

**Multi-user systems** (like web applications, banking systems, inventory management) have dozens or thousands of users reading and writing data simultaneously. Without protection, chaos ensues.

### The ACID Properties: Your Safety Net

ACID is the gold standard for reliable transactions. Here's what each letter guarantees:

**Atomicity: All or Nothing**

Every operation in a transaction either completes fully or leaves no trace. No partial updates allowed. It's like a light switch—it's either on or off, never halfway.

If you're updating 10 rows and the 7th one fails, the first 6 changes get undone automatically. The database reverts to its state before the transaction started.

**Consistency: Rules Are Never Broken**

The database enforces all rules, constraints, and triggers. You can't violate a foreign key, exceed a check constraint, or break unique indexes—even mid-transaction.

Example: If accounts must have a positive balance, a transaction that would create a negative balance gets rejected. The database moves from one valid state to another valid state, never through an invalid intermediate state.

**Isolation: Transactions Don't See Each Other's Dirty Laundry**

While your transaction is running, other transactions can't see your uncommitted changes. Each transaction operates as if it's alone in the database—no interference, no peeking at half-finished work.

This prevents bizarre bugs where calculations change mid-flight because someone else modified the data you're reading.

**Durability: Committed Means Permanent**

Once you see "Transaction committed," that data is permanent—even if the server explodes 2 seconds later. The database writes to durable storage and maintains logs to recover committed work after any failure.

### Transaction Lifecycle

```
[Active] → [Partially Committed] → [Committed] → [Terminated]
              ↓
          [Aborted/Rolled Back]
```

**Active**: Transaction is running, executing queries

**Partially Committed**: All operations finished, waiting for final commit

**Committed**: Changes are permanent and visible to others

**Aborted**: Something went wrong; all changes are undone

**Terminated**: Transaction is completely finished (after commit or abort)

**Key Commands:**

```sql
BEGIN;                    -- Start transaction
COMMIT;                   -- Make changes permanent
ROLLBACK;                 -- Undo everything
SAVEPOINT checkpoint1;    -- Mark a point to return to
ROLLBACK TO checkpoint1;  -- Undo to savepoint, keep the rest
```

Savepoints let you undo part of a transaction without throwing everything away—useful for complex operations with multiple stages.

### The Transaction Log: Your Safety Recorder

The database maintains detailed logs of every change, allowing it to:

**Undo uncommitted work** (rollback): If a transaction aborts, the log contains the "before" values needed to reverse changes.

**Redo committed work** (recovery): After a crash, the log contains the "after" values needed to replay committed transactions that didn't make it to disk yet.

Think of it like a security camera recording everything. If something goes wrong, you can rewind or replay events to restore the correct state.

---

## Part 2: Concurrency Problems

When multiple transactions run simultaneously without protection, several nightmares can occur:

### Dirty Read: Reading Uncommitted Garbage

Transaction 1 changes a value but hasn't committed. Transaction 2 reads that changed value. Transaction 1 rolls back. Transaction 2 just read data that never actually existed—a ghost value.

**Example:**
```
T1: UPDATE products SET price = 50 WHERE id = 1;  (not committed yet)
T2: SELECT price FROM products WHERE id = 1;      (reads 50)
T1: ROLLBACK;                                      (price goes back to 100)
```

Transaction 2 made decisions based on a price that was never real.

### Unrepeatable Read: Data Changes Mid-Transaction

You read a value, do some calculations, then read it again—but someone else changed it between your reads. Your data is inconsistent within a single transaction.

**Example:**
```
T1: SELECT balance FROM accounts WHERE id = 'Alice';  (reads $1000)
T2: UPDATE accounts SET balance = 500 WHERE id = 'Alice';
T2: COMMIT;
T1: SELECT balance FROM accounts WHERE id = 'Alice';  (now reads $500!)
```

Transaction 1's two SELECT statements returned different values. If T1 was calculating interest or making decisions based on that balance, it's now working with contradictory information.

### Phantom Read: Rows Appear Out of Nowhere

You query for all rows matching a condition, process them, then query again—but new rows appeared that match your condition. It's like counting people in a room, and suddenly more people have materialized.

**Example:**
```
T1: SELECT COUNT(*) FROM orders WHERE status = 'pending';  (returns 5)
T2: INSERT INTO orders (status) VALUES ('pending');
T2: COMMIT;
T1: SELECT COUNT(*) FROM orders WHERE status = 'pending';  (returns 6!)
```

Between T1's queries, a phantom row appeared. If T1 was generating a report or making aggregate calculations, the data changed underneath it.

### Lost Update: Your Changes Get Overwritten

Two transactions read the same value, both modify it based on what they read, and the second write overwrites the first. One update is lost forever.

**Example:**
```
T1: SELECT quantity FROM inventory WHERE product_id = 42;  (reads 10)
T2: SELECT quantity FROM inventory WHERE product_id = 42;  (reads 10)
T1: UPDATE inventory SET quantity = 9 WHERE product_id = 42;  (10 - 1)
T2: UPDATE inventory SET quantity = 8 WHERE product_id = 42;  (10 - 2)
```

T2's update overwrote T1's. Instead of 8 items remaining (10 - 1 - 2), we have 8, and T1's decrement was lost.

---

## Part 3: Isolation Levels

Databases offer different **isolation levels** that trade off between safety and performance. Higher isolation prevents more problems but runs slower because transactions wait for each other more.

### Read Uncommitted (Lowest Isolation)

**Zero protection.** You can read uncommitted data from other transactions. Dirty reads, unrepeatable reads, and phantom reads all happen freely.

**When to use:** Almost never. Only for situations where approximate results are acceptable and speed is critical—like real-time analytics dashboards that don't need exact values.

### Read Committed

**Prevents dirty reads** but allows unrepeatable and phantom reads.

You can only read data that's been committed. If another transaction is writing, you either wait or see the old value—never the in-progress value.

**When to use:** This is the default in many databases (including PostgreSQL). Good for most applications where you want to avoid reading garbage but can tolerate data changing between reads.

### Repeatable Read

**Prevents dirty reads and unrepeatable reads** but allows phantoms.

Within a transaction, if you read a row multiple times, you'll always get the same value—even if other transactions change it. However, new rows can still appear.

**When to use:** Financial calculations, reports, or any situation where you need a consistent snapshot of specific rows throughout your transaction.

### Serializable (Highest Isolation)

**Prevents everything:** dirty reads, unrepeatable reads, and phantom reads.

Transactions execute as if they ran one after another in series—complete isolation. No concurrency anomalies possible.

**When to use:** Critical operations like financial transactions, seat bookings, inventory management where correctness is non-negotiable.

**The tradeoff:** Slowest performance. Transactions may wait in line or get aborted and retried if conflicts are detected.

**Setting isolation level:**
```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN;
-- your queries here
COMMIT;
```

### MVCC: The Clever Alternative to Locking

**Multi-Version Concurrency Control (MVCC)** is a brilliant technique used by PostgreSQL, MySQL InnoDB, and others. Instead of locking data and making readers wait, the database keeps multiple versions of each row.

**How it works:**
- When you UPDATE a row, the database doesn't overwrite it—it creates a new version
- Older transactions continue seeing the old version
- Newer transactions see the new version
- Eventually, obsolete versions get cleaned up

**The magic:** Readers never block writers, and writers never block readers. Your SELECT queries can run at full speed even while hundreds of UPDATEs are happening.

This is why PostgreSQL can handle massive concurrent workloads gracefully—everyone gets their own consistent snapshot without waiting.

---

## Part 4: Locking Mechanisms

Locks are the database's way of saying "I'm using this right now—wait your turn."

### Basic Lock Types

**Shared Lock (S)**: Multiple transactions can hold shared locks simultaneously. Used for reading data.

Think of it like multiple people reading the same book at the library—everyone can look at it together, but nobody can check it out and take it home.

**Exclusive Lock (X)**: Only one transaction can hold an exclusive lock. Used for writing data.

Like checking out the book—only one person can have it. Everyone else must wait.

**The rules:**
- Many shared locks can coexist (readers don't block readers)
- An exclusive lock blocks everything (writers block everyone)
- Shared locks block exclusive locks (readers block writers)

### Lock Granularity: How Much to Lock?

Databases can lock at different levels:

```
Database (entire system)
   ↓
Table (all rows)
   ↓
Page/Block (chunk of rows stored together)
   ↓
Row (single record)
   ↓
Field (individual column—rare)
```

**Coarse-grained locking** (table-level): Simple and fast to manage, but causes lots of waiting. Updating one row locks the entire table.

**Fine-grained locking** (row-level): Maximum concurrency—transactions only wait if they want the exact same row. But there's overhead managing thousands of individual locks.

Modern databases like PostgreSQL default to row-level locking, giving you the best balance.

### Intent Locks: Declaring Your Intentions

Intent locks prevent conflicts in hierarchical locking. Before locking a row, you declare your intent at the table level.

**IS (Intent Shared)**: "I plan to read some rows"

**IX (Intent Exclusive)**: "I plan to write some rows"

**SIX (Shared with Intent Exclusive)**: "I'm reading the whole table but will write to a few rows"

This prevents someone from locking the entire table while you're working on individual rows—they can see your intent lock and wait appropriately.

### Gap Locks and Next-Key Locks: Preventing Phantoms

These locks protect the **space between rows**, not just the rows themselves.

If you query `WHERE id BETWEEN 10 AND 20`, a next-key lock prevents other transactions from INSERTing id=15 during your transaction. Without this, you'd get phantom reads.

These are crucial for serializable isolation and are used automatically in higher isolation levels.

### Deadlocks: The Eternal Standoff

A deadlock occurs when transactions wait for each other in a cycle:

```
Transaction 1: holds lock on row A, wants lock on row B
Transaction 2: holds lock on row B, wants lock on row A
```

Both wait forever. Neither can proceed.

**Database solutions:**
- **Timeout**: After waiting X seconds, abort one transaction
- **Deadlock detection**: Database periodically checks for cycles in the wait graph and kills one transaction
- **Deadlock prevention**: Force transactions to acquire locks in a specific order

**How to minimize deadlocks:**
- Access tables in the same order across all transactions
- Keep transactions short
- Use lower isolation levels when appropriate

### Starvation: Always Waiting, Never Served

Some transactions might wait indefinitely if newer, higher-priority transactions keep jumping ahead. The database needs fairness policies to ensure everyone eventually gets served.

---

## Part 5: Two-Phase Locking (2PL)

Two-phase locking is the most common protocol for ensuring **serializability**—making concurrent transactions behave as if they ran one at a time.

### The Two Phases

**Growing Phase**: Transaction acquires locks as needed but never releases any

**Shrinking Phase**: Transaction releases locks but cannot acquire new ones

**The rule:** Once you release your first lock, you can never acquire another lock in that transaction.

**Why this works:** This prevents situations where you release a lock, someone else changes the data, then you acquire another lock and read inconsistent data.

### Variants

**Strict 2PL**: Don't release ANY locks until commit time. Prevents cascading rollbacks—if you abort, nobody else has to abort because they never saw your changes.

**Rigorous 2PL**: Same as strict, but even stricter timing guarantees.

Most production databases use strict two-phase locking under the hood.

---

## Part 6: Timestamp-Based Concurrency

An alternative to locking: give each transaction a timestamp when it starts, and use timestamps to decide what's allowed.

### The Principle

Older transactions have priority. If a newer transaction tries to read data written by a future transaction, or write over data read by an older transaction, it gets aborted.

**Advantages:**
- No deadlocks (transactions never wait for locks)
- Good for read-heavy workloads

**Disadvantages:**
- More aborts and retries in high-conflict scenarios
- Overhead maintaining timestamps

### Thomas' Write Rule

A clever optimization: if a transaction tries to write to data that's already been overwritten by a newer transaction, just ignore the write—it's obsolete anyway. This prevents unnecessary aborts.

---

## Part 7: Optimistic Concurrency Control

The philosophy: **assume conflicts are rare**. Don't lock anything. Just check for conflicts before committing.

### Three Phases

**1. Read Phase**: Execute all reads and writes in a private workspace. No locks acquired.

**2. Validation Phase**: Before committing, check if any data you read was modified by another transaction.

**3. Write Phase**: If validation passes, apply your changes. If validation fails, abort and retry.

**When this works brilliantly:**
- Read-heavy workloads (analytics, reporting)
- Low-conflict scenarios
- Transactions that rarely touch the same data

**When this fails:**
- High-conflict workloads (everyone fighting over the same inventory)
- Lots of wasted work if validation keeps failing

---

## Part 8: Schedules and Serializability

A **schedule** is the interleaved execution order of operations from multiple concurrent transactions.

### Serial vs Interleaved Schedules

**Serial schedule**: Transactions run one at a time, no overlap. Always correct but slow.

**Interleaved schedule**: Operations from different transactions mix together. Fast but must be carefully controlled.

### Conflict Serializability

Two operations **conflict** if:
- They're from different transactions
- They operate on the same data
- At least one is a write

A schedule is **conflict serializable** if you can reorder non-conflicting operations to produce a serial schedule.

This is the foundation of serializability testing—databases analyze schedules to ensure they're equivalent to some serial execution.

### Recoverability

Not all serializable schedules are recoverable. If Transaction 2 reads data written by Transaction 1, then Transaction 2 commits before Transaction 1, you have a problem—what if Transaction 1 aborts? You can't undo Transaction 2's commit.

**Recoverable schedule**: Don't commit until all transactions you've read from have committed.

**Cascadeless schedule**: Never read uncommitted data. Prevents cascading aborts where one abort forces others to abort.

**Strict schedule**: Don't read OR write data touched by uncommitted transactions.

Strict schedules are the safest but most restrictive.

---

## Key Takeaways

1. **Transactions are your safety guarantee**—they ensure operations complete fully or not at all, protecting against corruption.

2. **ACID properties aren't negotiable** for systems handling money, reservations, or any critical data.

3. **Isolation levels trade safety for speed**—choose based on your application's needs. When in doubt, start with Read Committed.

4. **MVCC is magic**—it provides excellent isolation without sacrificing read performance.

5. **Locking prevents chaos** but can cause deadlocks. Keep transactions short and access resources in consistent order.

6. **Serializability is the gold standard**—concurrent execution that's equivalent to serial execution.

7. **Optimistic concurrency works great** for low-conflict scenarios but wastes work in high-conflict ones.

Understanding these concepts separates developers who write database code from those who write **reliable, scalable** database applications. Master these fundamentals, and you'll be equipped to build systems that handle millions of users without losing a penny or double-booking a seat.