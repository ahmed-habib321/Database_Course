# 🚀 Phase 11 — Stored Procedures, Triggers & Cursors
Server-side business logic & event-driven DB code

Phase 11 is where we bring business logic into the database, so certain operations happen automatically on the server side, rather than leaving everything to the application. This is great for enforcing rules, auditing, or automating repetitive tasks.

## 1️⃣ Stored Procedures
Stored Procedures are predefined SQL code blocks stored in the database and executed on demand.  
They can have parameters, perform complex operations, and improve consistency and performance.

Syntax Example (MySQL / SQL Server style)
```postgresql
CREATE PROCEDURE AddOrder(IN cust_id INT, IN amt DECIMAL(10,2))
BEGIN
INSERT INTO orders(customer_id, amount) VALUES (cust_id, amt);
END;
```
Key Points
- Can take input/output parameters 
- Can perform transactions, loops, conditions 
- Precompiled → faster than sending multiple queries from app 
- Centralizes business rules → consistency

## 2️⃣ Functions vs Procedures
| Feature                 | Function                     | Procedure                                       |
| ----------------------- | ---------------------------- | ----------------------------------------------- |
| Returns value           | ✔ Yes                        | ❌ Optional                                      |
| Can be used in SELECT   | ✔ Yes                        | ❌ No                                            |
| Can execute transaction | ❌ No                         | ✔ Yes                                           |
| Use case                | Calculations, derived values | Business logic, multiple steps, inserts/updates |

## 3️⃣ Triggers
Triggers are automatic actions fired before or after certain events (INSERT, UPDATE, DELETE) on a table.

Syntax Example
```postgresql
CREATE TRIGGER trg_after_order
AFTER INSERT ON orders
FOR EACH ROW
BEGIN
INSERT INTO audit_log(table_name, action, created_at)
VALUES ('orders', 'INSERT', NOW());
END;
```
Trigger Types

| Type   | When it fires            |
| ------ | ------------------------ |
| BEFORE | Before the DML operation |
| AFTER  | After the DML operation  |

Common Use Cases
- Auditing (log changes)
- Enforcing business rules 
- Maintaining summary tables 
- Cascading actions (like updating inventory when order placed)

## 4️⃣ Auditing System Example
Suppose we want to track changes to the `employees` table:

```postgresql
CREATE TRIGGER trg_emp_update
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
INSERT INTO employee_audit(emp_id, old_salary, new_salary, changed_at)
VALUES (OLD.emp_id, OLD.salary, NEW.salary, NOW());
END;
```
- OLD → values before update 
- NEW → values after update 
- Records every salary change automatically

## 5️⃣ Cursors (Intro)
Cursors allow row-by-row processing inside procedures or scripts, useful when set-based operations are not enough.
```postgresql
DECLARE cur CURSOR FOR
SELECT id, amount FROM orders WHERE status='pending';

OPEN cur;

FETCH NEXT FROM cur INTO @order_id, @amount;

-- Process each row individually

CLOSE cur;
```
⚠️ Caution: Cursors are slower than set-based operations.  
✅ Use only for sequential processing, e.g., sending emails, processing batch jobs.

## 6️⃣ When to Avoid Putting Business Logic in DB
While stored procedures and triggers are powerful, overusing them can cause:

- Vendor lock-in (procedures differ between MySQL, PostgreSQL, SQL Server)
- Harder to maintain / version control 
- Performance issues if too many triggers fire per operation 
- Testing difficulties (logic is split between app & DB)

Rule of Thumb:
- Use DB logic for: integrity rules, auditing, simple automation 
- Use application code for: complex workflows, reporting, external service calls