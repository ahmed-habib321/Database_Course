# SQL Basics: Complete Beginner's Guide

## What is SQL?

**SQL (Structured Query Language)** is the standard language for communicating with relational databases. Whether you're creating tables, inserting data, or running complex queries, you're using SQL.

**Think of SQL as:** The language databases speak. Just like you use English to talk to people, you use SQL to talk to databases.

---

## 📚 The Five Categories of SQL

SQL commands are organized into five main categories, each with a specific purpose:

| Category | Acronym | Purpose | Example Commands |
|----------|---------|---------|------------------|
| **Data Definition Language** | DDL | Define and modify database structure | CREATE, ALTER, DROP |
| **Data Manipulation Language** | DML | Change the data inside tables | INSERT, UPDATE, DELETE |
| **Data Query Language** | DQL | Retrieve and read data | SELECT |
| **Data Control Language** | DCL | Manage permissions | GRANT, REVOKE |
| **Transaction Control Language** | TCL | Manage transactions | COMMIT, ROLLBACK |

**This guide focuses on DDL, DML, and DQL** — the foundational skills every SQL user needs.

---

## 🏗️ DDL: Data Definition Language

DDL commands define the **structure** of your database — the tables, columns, and data types.

**Key Concept:** DDL doesn't touch the data itself, it sets up the containers that will hold data.

---

### CREATE: Build New Tables

**Purpose:** Create a new table with specified columns and data types.

**Basic Syntax:**
```sql
CREATE TABLE table_name (
    column1 datatype constraints,
    column2 datatype constraints,
    ...
);
```

**Example: Creating a Students Table**
```sql
CREATE TABLE Students (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT,
    city VARCHAR(50) DEFAULT 'Cairo',
    email VARCHAR(120) UNIQUE
);
```

**Breaking it down:**
- `id INT PRIMARY KEY` — An integer that uniquely identifies each student
- `name VARCHAR(100) NOT NULL` — Text up to 100 characters, cannot be empty
- `age INT` — An integer, can be empty (NULL)
- `city VARCHAR(50) DEFAULT 'Cairo'` — Text with automatic value if none provided
- `email VARCHAR(120) UNIQUE` — No two students can have the same email

---

### ALTER: Modify Existing Tables

**Purpose:** Change the structure of an existing table without recreating it.

**Add a new column:**
```sql
ALTER TABLE Students 
ADD phone_number VARCHAR(15);
```
*Now every student record has space for a phone number.*

**Modify a column's data type:**
```sql
ALTER TABLE Students 
MODIFY age SMALLINT;
```
*Changes age from INT to SMALLINT (saves storage).*

**Note:** Syntax varies by database:
- MySQL/MariaDB: `MODIFY`
- PostgreSQL: `ALTER COLUMN age TYPE SMALLINT`
- SQL Server: `ALTER COLUMN age SMALLINT`

**Rename a column:**
```sql
ALTER TABLE Students 
RENAME COLUMN city TO hometown;
```

**Drop (delete) a column:**
```sql
ALTER TABLE Students 
DROP COLUMN email;
```
⚠️ **Warning:** This permanently deletes the column and all its data!

---

### DROP: Delete Tables

**Purpose:** Completely remove a table and all its data from the database.

```sql
DROP TABLE Students;
```

⚠️ **Critical Warning:** 
- This is **permanent** and **irreversible** in most systems
- All data in the table is immediately lost
- Always backup before dropping tables in production

**Safer alternative:** Use `DROP TABLE IF EXISTS` to avoid errors if the table doesn't exist:
```sql
DROP TABLE IF EXISTS Students;
```

---

### TRUNCATE: Empty a Table

**Purpose:** Delete all rows but keep the table structure.

```sql
TRUNCATE TABLE Students;
```

**TRUNCATE vs DELETE:**
- `TRUNCATE` — Fast, resets auto-increment counters, cannot be rolled back
- `DELETE` — Slower, keeps auto-increment state, can be rolled back

---

## 📝 DML: Data Manipulation Language

DML commands work with the **actual data** inside tables — adding, changing, and removing records.

---

### INSERT: Add New Data

**Purpose:** Add new rows to a table.

**Insert a single row:**
```sql
INSERT INTO Students (id, name, age, city)
VALUES (1, 'Ahmed', 22, 'Cairo');
```

**Insert multiple rows at once:**
```sql
INSERT INTO Students (id, name, age, city)
VALUES 
    (2, 'Sara', 25, 'Alexandria'),
    (3, 'Omar', 21, 'Cairo'),
    (4, 'Layla', 23, 'Giza');
```

**Shortcut:** If inserting values for ALL columns in order, you can omit column names:
```sql
INSERT INTO Students
VALUES (5, 'Youssef', 24, 'Cairo');
```

**Insert from another table:**
```sql
INSERT INTO ArchivedStudents
SELECT * FROM Students
WHERE age > 25;
```

---

### UPDATE: Modify Existing Data

**Purpose:** Change values in existing rows.

**Update a specific record:**
```sql
UPDATE Students
SET city = 'Giza'
WHERE id = 1;
```
*Ahmed's city is now Giza instead of Cairo.*

**Update multiple columns:**
```sql
UPDATE Students
SET age = 23, city = 'Alexandria'
WHERE id = 2;
```

**Update multiple rows:**
```sql
UPDATE Students
SET city = 'Cairo'
WHERE age < 22;
```
*All students under 22 now have city = 'Cairo'.*

**Update all rows:**
```sql
UPDATE Students
SET city = 'Egypt';
```
⚠️ **Danger:** Without `WHERE`, this updates EVERY row!

---

### DELETE: Remove Data

**Purpose:** Remove specific rows from a table.

**Delete specific records:**
```sql
DELETE FROM Students
WHERE age < 20;
```
*Removes all students younger than 20.*

**Delete based on multiple conditions:**
```sql
DELETE FROM Students
WHERE city = 'Cairo' AND age > 25;
```

**Delete all rows (keep structure):**
```sql
DELETE FROM Students;
```
⚠️ **Warning:** This empties the table but keeps the structure intact. Use `TRUNCATE` for better performance if deleting all rows.

**Pro tip:** Always test with `SELECT` first:
```sql
-- First, see what you'll delete
SELECT * FROM Students WHERE age < 20;

-- Then delete
DELETE FROM Students WHERE age < 20;
```

---

## 🔍 DQL: Data Query Language (SELECT)

DQL is all about **retrieving** data. The `SELECT` statement is the most used SQL command.

---

### Basic SELECT

**Select all columns:**
```sql
SELECT * FROM Students;
```
*The asterisk (*) means "all columns".*

**Select specific columns:**
```sql
SELECT name, city FROM Students;
```
*Only shows name and city.*

**Select with calculated columns:**
```sql
SELECT name, age, age + 5 AS age_in_5_years
FROM Students;
```

---

### WHERE: Filtering Rows

**Purpose:** Show only rows that meet specific conditions.

**Exact match:**
```sql
SELECT * FROM Students
WHERE city = 'Cairo';
```

**Comparison operators:**
```sql
SELECT * FROM Students
WHERE age >= 22;
```

**Available operators:** `=`, `!=` or `<>`, `>`, `<`, `>=`, `<=`

---

### Pattern Matching with LIKE

**Purpose:** Find text that matches a pattern.

**Wildcards:**
- `%` — Matches any sequence of characters (including zero)
- `_` — Matches exactly one character

**Starts with 'A':**
```sql
SELECT * FROM Students
WHERE name LIKE 'A%';
```
*Finds: Ahmed, Ali, Aisha, but not Sara*

**Ends with 'a':**
```sql
SELECT * FROM Students
WHERE name LIKE '%a';
```
*Finds: Sara, Layla, but not Ahmed*

**Contains 'ar':**
```sql
SELECT * FROM Students
WHERE name LIKE '%ar%';
```
*Finds: Sara, Omar (anywhere in the name)*

**Exactly 4 characters:**
```sql
SELECT * FROM Students
WHERE name LIKE '____';
```
*Finds: Sara, Omar (exactly 4 characters)*

**Case sensitivity:** Depends on database configuration. Most systems are case-insensitive by default.

---

### Multiple Value Matching with IN

**Purpose:** Check if a value matches any item in a list.

```sql
SELECT * FROM Students
WHERE city IN ('Cairo', 'Giza', 'Alexandria');
```
*Much cleaner than: `city = 'Cairo' OR city = 'Giza' OR city = 'Alexandria'`*

**Opposite (NOT IN):**
```sql
SELECT * FROM Students
WHERE city NOT IN ('Cairo', 'Giza');
```

---

### Range Checking with BETWEEN

**Purpose:** Find values within a range (inclusive).

```sql
SELECT * FROM Students
WHERE age BETWEEN 20 AND 25;
```
*Includes students aged 20, 21, 22, 23, 24, and 25.*

**Equivalent to:**
```sql
SELECT * FROM Students
WHERE age >= 20 AND age <= 25;
```

---

### Combining Conditions: AND / OR / NOT

**AND — All conditions must be true:**
```sql
SELECT * FROM Students
WHERE age >= 21 AND city = 'Cairo';
```
*Both conditions required.*

**OR — At least one condition must be true:**
```sql
SELECT * FROM Students
WHERE city = 'Cairo' OR city = 'Giza';
```

**NOT — Negates a condition:**
```sql
SELECT * FROM Students
WHERE NOT city = 'Cairo';
```

**Complex combinations (use parentheses):**
```sql
SELECT * FROM Students
WHERE (city = 'Cairo' OR city = 'Giza')
  AND age >= 22;
```
*Students from Cairo or Giza who are 22+.*

---

### Handling NULL Values

**Check for NULL:**
```sql
SELECT * FROM Students
WHERE email IS NULL;
```
*Finds students without an email.*

**Check for non-NULL:**
```sql
SELECT * FROM Students
WHERE email IS NOT NULL;
```

⚠️ **Common mistake:** `WHERE email = NULL` **doesn't work!** Always use `IS NULL`.

---

## 📊 Sorting and Limiting Results

### ORDER BY: Sort Results

**Purpose:** Arrange results in ascending or descending order.

**Ascending (default):**
```sql
SELECT * FROM Students
ORDER BY age;
```
*Youngest to oldest.*

**Descending:**
```sql
SELECT * FROM Students
ORDER BY age DESC;
```
*Oldest to youngest.*

**Sort by multiple columns:**
```sql
SELECT * FROM Students
ORDER BY city ASC, age DESC;
```
*First by city (A-Z), then by age (high-low) within each city.*

---

### LIMIT: Restrict Number of Results

**Purpose:** Return only the first N rows.

**MySQL/PostgreSQL/SQLite:**
```sql
SELECT * FROM Students
LIMIT 5;
```
*Returns first 5 students.*

**SQL Server:**
```sql
SELECT TOP 5 * FROM Students;
```

**Oracle:**
```sql
SELECT * FROM Students
WHERE ROWNUM <= 5;
```

**Get top N with specific order:**
```sql
SELECT * FROM Students
ORDER BY age DESC
LIMIT 3;
```
*The 3 oldest students.*

**Pagination (skip and take):**
```sql
SELECT * FROM Students
ORDER BY id
LIMIT 10 OFFSET 20;
```
*Skip first 20 rows, return next 10 (useful for page 3 of results).*

---

## 🔒 Constraints: Protecting Data Integrity

Constraints are **rules** that enforce data quality and consistency.

### Common Constraints

| Constraint | Purpose | Example |
|------------|---------|---------|
| `NOT NULL` | Column cannot be empty | `name VARCHAR(100) NOT NULL` |
| `UNIQUE` | No duplicate values allowed | `email VARCHAR(120) UNIQUE` |
| `PRIMARY KEY` | Unique identifier (NOT NULL + UNIQUE) | `id INT PRIMARY KEY` |
| `FOREIGN KEY` | Enforces relationship between tables | `FOREIGN KEY (dept_id) REFERENCES Departments(id)` |
| `CHECK` | Custom validation rule | `CHECK (age >= 18)` |
| `DEFAULT` | Automatic value if none provided | `city VARCHAR(50) DEFAULT 'Cairo'` |

---

### Example: Table with Multiple Constraints

```sql
CREATE TABLE Employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    salary DECIMAL(10, 2) CHECK (salary > 0),
    hire_date DATE DEFAULT CURRENT_DATE,
    department VARCHAR(50) DEFAULT 'HR',
    manager_id INT,
    FOREIGN KEY (manager_id) REFERENCES Employees(emp_id)
);
```

**What this enforces:**
- `emp_id` must be unique and not null
- `name` cannot be empty
- `email` must be unique and not empty
- `salary` must be positive
- `hire_date` defaults to today if not specified
- `department` defaults to 'HR'
- `manager_id` must reference another valid employee (or be NULL)

---

### Adding Constraints to Existing Tables

**Add UNIQUE constraint:**
```sql
ALTER TABLE Students
ADD CONSTRAINT unique_email UNIQUE (email);
```

**Add CHECK constraint:**
```sql
ALTER TABLE Students
ADD CONSTRAINT check_age CHECK (age >= 18);
```

**Add FOREIGN KEY:**
```sql
ALTER TABLE Enrollments
ADD CONSTRAINT fk_student
FOREIGN KEY (student_id) REFERENCES Students(id);
```

---

## 👁️ Views: Virtual Tables

**What is a view?** A saved SELECT query that acts like a table.

**Key Concept:** Views don't store data — they're dynamic windows into existing tables.

---

### Why Use Views?

1. **Simplify complex queries** — Save and reuse complicated joins
2. **Security** — Show users only specific columns/rows
3. **Abstraction** — Hide database complexity
4. **Consistency** — Everyone uses the same query logic

---

### Creating Views

**Basic view:**
```sql
CREATE VIEW CairoStudents AS
SELECT id, name, age
FROM Students
WHERE city = 'Cairo';
```

**Using the view:**
```sql
SELECT * FROM CairoStudents;
```
*It looks like a table, but it's actually running the stored query.*

**View with joins:**
```sql
CREATE VIEW StudentEnrollments AS
SELECT s.name, s.city, e.course_name, e.grade
FROM Students s
JOIN Enrollments e ON s.id = e.student_id;
```

---

### Updating Views

**Replace view definition:**
```sql
CREATE OR REPLACE VIEW CairoStudents AS
SELECT id, name, age, email
FROM Students
WHERE city = 'Cairo';
```

**Alternative syntax (some databases):**
```sql
ALTER VIEW CairoStudents AS
SELECT id, name, age, email
FROM Students
WHERE city = 'Cairo';
```

---

### Dropping Views

```sql
DROP VIEW CairoStudents;
```

**Safe version:**
```sql
DROP VIEW IF EXISTS CairoStudents;
```

---

### Updating Data Through Views

**Sometimes possible** (if view meets certain conditions):
```sql
UPDATE CairoStudents
SET age = 23
WHERE id = 1;
```
*This actually updates the underlying Students table.*

**When updates work:**
- View is based on a single table
- No aggregate functions (COUNT, SUM, etc.)
- No GROUP BY or DISTINCT
- All NOT NULL columns are included

---

## 🎯 Practical Examples: Putting It All Together

### Example 1: Student Management System

```sql
-- Create the table
CREATE TABLE Students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    age INT CHECK (age >= 16),
    city VARCHAR(50) DEFAULT 'Cairo',
    enrollment_date DATE DEFAULT CURRENT_DATE
);

-- Insert sample data
INSERT INTO Students (name, email, age, city)
VALUES 
    ('Ahmed Ali', 'ahmed@email.com', 22, 'Cairo'),
    ('Sara Mohamed', 'sara@email.com', 20, 'Alexandria'),
    ('Omar Hassan', 'omar@email.com', 19, 'Cairo');

-- Query: Find Cairo students over 20
SELECT name, age 
FROM Students
WHERE city = 'Cairo' AND age > 20
ORDER BY age DESC;

-- Create a view for active students
CREATE VIEW ActiveStudents AS
SELECT name, email, city
FROM Students
WHERE enrollment_date >= DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR);
```

### Example 2: Employee Database

```sql
-- Create departments table
CREATE TABLE Departments (
    dept_id INT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL UNIQUE
);

-- Create employees table with foreign key
CREATE TABLE Employees (
    emp_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    salary DECIMAL(10, 2) CHECK (salary > 0),
    dept_id INT NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES Departments(dept_id)
);

-- View: High earners
CREATE VIEW HighEarners AS
SELECT e.name, e.salary, d.dept_name
FROM Employees e
JOIN Departments d ON e.dept_id = d.dept_id
WHERE e.salary > 50000;
```

---

## 💡 Best Practices

1. **Always use WHERE with UPDATE/DELETE** — Avoid accidentally changing all rows
2. **Test SELECT before DELETE** — Preview what will be deleted
3. **Use constraints liberally** — Prevent bad data from entering the database
4. **Name constraints explicitly** — Makes errors easier to debug
5. **Backup before DROP/TRUNCATE** — These operations are permanent
6. **Use views for complex logic** — Don't repeat complicated queries
7. **Be consistent with naming** — Use snake_case or camelCase, not both

---

## 🚀 Next Steps

You now know the fundamentals of SQL! You can:
- ✅ Create and modify database structures (DDL)
- ✅ Insert, update, and delete data (DML)
- ✅ Query and filter data effectively (DQL)
- ✅ Protect data integrity with constraints
- ✅ Simplify complex queries with views

**What's next?** Learn about:
- **Joins** (combining data from multiple tables)
- **Aggregate functions** (COUNT, SUM, AVG, etc.)
- **Subqueries** (queries within queries)
- **Indexes** (speeding up queries)
- **Transactions** (ensuring data consistency)