# 🚀 Phase 6 — Intermediate SQL
Phase 6 is where SQL stops being “just commands” and becomes a tool to express business logic and answer complex questions. Here’s a full explanation of each topic — simple, practical, and with examples.

## 1️⃣ Aggregate Functions
Used to perform calculations on multiple rows and return a single value.

| Function  | Purpose                 |
| --------- | ----------------------- |
| `COUNT()` | Number of rows          |
| `SUM()`   | Total of numeric column |
| `AVG()`   | Average value           |
| `MIN()`   | Minimum value           |
| `MAX()`   | Maximum value           |

Example: total revenue & number of orders:
```postgresql
SELECT COUNT(*) AS total_orders,
SUM(amount) AS total_revenue,
AVG(amount) AS avg_order_value
FROM orders;
```
## 2️⃣ GROUP BY & HAVING
- `GROUP BY` → groups rows by a column. 
- `HAVING` → filters after grouping (like `WHERE` but for groups).

Example: total sales per customer but only those with orders > 5000:
```postgresql
SELECT customer_id, SUM(amount) AS total_spent
FROM orders
GROUP BY customer_id
HAVING SUM(amount) > 5000;
```
## 3️⃣ Joins
To combine data across multiple tables.

| Join Type  | Description                                    | Result        |
| ---------- | ---------------------------------------------- | ------------- |
| INNER JOIN | Matching rows in both tables                   | Intersection  |
| LEFT JOIN  | All from left + matches from right             | Keep left     |
| RIGHT JOIN | All from right + matches from left             | Keep right    |
| FULL JOIN  | All records from both, matching where possible | Union of both |


Example:
```postgresql
SELECT c.name, o.amount
FROM customers c
LEFT JOIN orders o
ON c.id = o.customer_id;
```
➡️ shows all customers even if they have no orders.

## 4️⃣ Subqueries
Queries inside queries: used to compare, filter, or generate data dynamically.

### 📌 Non-Correlated Subquery
Independent; runs first.
```postgresql
SELECT name
FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);
```
### 📌 Correlated Subquery
Depends on outer query; runs for every row.
```postgresql
SELECT e1.name
FROM employees e1
WHERE salary > (
SELECT AVG(salary)
FROM employees e2
WHERE e1.department_id = e2.department_id
);
```
## 5️⃣ Views
A virtual table created from a query.
```postgresql
CREATE VIEW high_value_orders AS
SELECT * FROM orders WHERE amount > 1000;
```
Using it:
```postgresql
SELECT * FROM high_value_orders;
```
📌 Why use views?
✔️ Hides complexity   
✔️ Improves security (limit column visibility)  
✔️ Reusable queries

## 6️⃣ Materialized Views
Like views, but results are saved physically & can be refreshed.
```postgresql
CREATE MATERIALIZED VIEW sales_summary AS
SELECT customer_id, SUM(amount) AS total
FROM orders
GROUP BY customer_id;
```

| Pros                        | Cons                |
| --------------------------- | ------------------- |
| Faster reads                | Data can be stale   |
| Good for aggregated reports | Needs refresh logic |

## 7️⃣ Stored Procedures (Intro)
Reusable blocks of code stored on the DB server.  
Good for logic that must live on the database.
```postgresql
CREATE PROCEDURE AddOrder(IN cust INT, IN amt DECIMAL)
BEGIN
INSERT INTO orders(customer_id, amount)
VALUES (cust, amt);
END;
```

## 8️⃣ Function vs Procedure
| Feature                 | Function          | Procedure             |
| ----------------------- | ----------------- | --------------------- |
| Returns a value         | ✔️ Yes (required) | ❌ Not required        |
| Used in SELECT          | ✔️ Yes            | ❌ No                  |
| Purpose                 | calculation       | business workflow     |
| Has transaction control | ❌ No              | ✔️ Yes (BEGIN/COMMIT) |

Function example:
```postgresql
CREATE FUNCTION total_orders(cust INT)
RETURNS INT
RETURN (SELECT COUNT(*) FROM orders WHERE customer_id = cust);
```
## 9️⃣ Triggers (Intro)
Actions that happen automatically when events occur (INSERT, UPDATE, DELETE).
```postgresql
CREATE TRIGGER log_insert
AFTER INSERT ON orders
FOR EACH ROW
INSERT INTO audit_log(message)
VALUES ('New order added on ' || NOW());
```
Uses:

- enforcing rules 
- audits 
- auto-updates

## 🔟 Cursors (Intro)
Used to iterate through rows one-by-one (like a loop).
Mostly used inside stored procedures.
```postgresql
DECLARE cursor_example CURSOR FOR
SELECT name FROM employees;
```
💡 Not used often — set-based queries are faster.  
Use only when you must process rows sequentially.

## 1️⃣1️⃣ SQL/PSM
PSM = Persistent Stored Modules  
Standard for creating functions, procedures, triggers, cursors in SQL.

- Adds procedural programming features:
  - Variables 
  - Loops 
  - Conditions (IF, CASE)
  - Exception handling