# 🚀 Phase 5 — SQL Basics
## 🧱 SQL Categories
| Category                             | Purpose                                    |
| ------------------------------------ | ------------------------------------------ |
| **DDL** (Data Definition Language)   | Create / Modify database structure         |
| **DML** (Data Manipulation Language) | Insert / Update / Delete data              |
| **DQL** (Data Query Language)        | Retrieve data                              |
| **Constraints**                      | Rules that protect data correctness        |
| **Views**                            | Saved queries that act like virtual tables |

## 📌 1️⃣ DDL — Structure Commands
### CREATE
```postgresql
CREATE TABLE Students (
id INT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
age INT,
city VARCHAR(50) DEFAULT 'Cairo'
);
```
### ALTER
Add a new column:
```postgresql
ALTER TABLE Students ADD email VARCHAR(120);
```
Modify a datatype:
```postgresql
ALTER TABLE Students MODIFY age SMALLINT;
```
Drop a column:
```postgresql
ALTER TABLE Students DROP COLUMN city;
```
### DROP
Delete the entire table:
```postgresql
DROP TABLE Students;
```
⚠️ Irreversible in most cases.

## 📌 2️⃣ DML — Manipulating Data
### INSERT
```postgresql
INSERT INTO Students (id, name, age)
VALUES (1, 'Ahmed', 22);
```
Multiple rows:
```postgresql
INSERT INTO Students
VALUES (2, 'Sara', 25, 'Alexandria'),
       (3, 'Omar', 21, 'Cairo');
```
### UPDATE
```postgresql
UPDATE Students
SET city = 'Giza'
WHERE id = 1;
```
### DELETE
```postgresql
DELETE FROM Students
WHERE age < 22;
```
⚠️ Missing WHERE deletes all rows.

## 📌 3️⃣ DQL — SELECT (Query Data)
### Basic Query
```postgresql
SELECT name, city FROM Students;
```
### WHERE filtering
```postgresql
SELECT * FROM Students
WHERE city = 'Cairo';
```
### LIKE (pattern matching)
```postgresql
SELECT * FROM Students
WHERE name LIKE 'A%';  -- starts with A
```
### IN (multiple matches)
```postgresql
SELECT * FROM Students
WHERE city IN ('Cairo', 'Giza');
```
### AND / OR
```postgresql
SELECT * FROM Students
WHERE age >= 21 AND city = 'Cairo';
```
## 📌 4️⃣ Sorting & Limits
### ORDER BY
```postgresql
SELECT * FROM Students
ORDER BY age DESC;
```
### LIMIT (MySQL / PostgreSQL)
```postgresql
SELECT * FROM Students LIMIT 5;
```
### TOP (SQL Server)
```postgresql
SELECT TOP 5 * FROM Students;
```
## 📌 5️⃣ Constraints — Data Protection
| Constraint    | Purpose                     |
| ------------- | --------------------------- |
| `NOT NULL`    | Prevents empty values       |
| `UNIQUE`      | Prevents duplicates         |
| `PRIMARY KEY` | Unique + NOT NULL           |
| `FOREIGN KEY` | Enforces relationships      |
| `CHECK`       | Custom rule                 |
| `DEFAULT`     | Auto value if none provided |

Example Table with Constraints
```postgresql
CREATE TABLE Employees
(
    emp_id     INT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    salary     DECIMAL(10, 2) CHECK (salary > 0),
    department VARCHAR(50) DEFAULT 'HR',
    manager_id INT,
    FOREIGN KEY (manager_id) REFERENCES Employees (emp_id)
);
```
## 📌 6️⃣ Views — Virtual Tables
Views are saved SELECT queries.

💡 They don’t store data — they show data from underlying tables dynamically.

### Create a View
```postgresql
CREATE VIEW CairoStudents AS
SELECT id, name
FROM Students
WHERE city = 'Cairo';
```
### Use the View
```postgresql
SELECT * FROM CairoStudents;
```
### Update View (SQL Standard)
```postgresql
ALTER VIEW CairoStudents AS
SELECT id, name, age
FROM Students
WHERE city = 'Cairo';
```
### Drop View
```postgresql
DROP VIEW CairoStudents;
```