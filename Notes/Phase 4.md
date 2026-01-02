# 🚀 Phase 4 — Relational Algebra
The theoretical foundation of SQL — how queries are logically processed.

Relational Algebra (RA) is a set of operations that take one or more relations (tables) as input and return a new relation as output.

It is the mathematical language behind SQL query processing.

## 📌 Unary Operations (One Relation Only)
| Operation   | Symbol    | SQL Equivalent  | Purpose                 |
| ----------- | --------- | --------------- | ----------------------- |
| **SELECT**  | σ (sigma) | `WHERE`         | Filter rows             |
| **PROJECT** | π (pi)    | `SELECT column` | Select columns          |
| **RENAME**  | ρ (rho)   | `AS`            | Rename relation/columns |

Examples
Table: Students(id, name, age, city)

| id | name  | age | city       |
| -- | ----- | --- | ---------- |
| 1  | Ahmed | 22  | Cairo      |
| 2  | Sara  | 25  | Alexandria |
| 3  | Omar  | 22  | Cairo      |

SELECT (σ) → Filter rows
```postgresql
σ city = 'Cairo' (Students)
```
Result:

| id | name  | age | city  |
| -- | ----- | --- | ----- |
| 1  | Ahmed | 22  | Cairo |
| 3  | Omar  | 22  | Cairo |

PROJECT (π) → Pick columns
```postgresql
π name, city (Students)
```
Result:

| name  | city       |
| ----- | ---------- |
| Ahmed | Cairo      |
| Sara  | Alexandria |
| Omar  | Cairo      |

RENAME (ρ) → Rename relation
```postgresql
ρ S(name, age) (Students)
```
Renames the table & attributes.

## 📌 Set Operations (Two Relations)
⚠️ Conditions: Relations must be union-compatible (same columns & types)

| Operation             | Symbol | SQL Equivalent     | Purpose                          |
| --------------------- | ------ | ------------------ | -------------------------------- |
| **UNION**             | ∪      | `UNION`            | Combine rows (remove duplicates) |
| **INTERSECTION**      | ∩      | `INTERSECT`        | Common rows                      |
| **DIFFERENCE**        | −      | `EXCEPT` / `MINUS` | Rows in A but not in B           |
| **CARTESIAN PRODUCT** | ×      | None (implicit)    | Pair each row with every row     |

Example

Students_Course_A ∪ Students_Course_B → all students taking course A or B

### Cartesian Product (×)
```postgresql
Students × Courses
```
If Students has 3 rows and Courses has 4 rows → result = 12 rows.

💡 Basis for JOIN operations.

## 📌 Join Operations
| Join Type        | Definition                                            | SQL Equivalent              |
| ---------------- | ----------------------------------------------------- | --------------------------- |
| **Inner Join**   | Matches where keys equal                              | `INNER JOIN`                |
| **Natural Join** | Auto-match columns with same name                     | `NATURAL JOIN`              |
| **Equi Join**    | Join using equality condition                         | `JOIN ON A.id = B.id`       |
| **Outer Join**   | Keep unmatched rows too                               | `LEFT / RIGHT / FULL OUTER` |
| **Division**     | Find rows related to **all** rows of another relation | No direct SQL keyword       |

### Inner Join (⋈)
```postgresql
Students ⋈ (Students.id = Enroll.student_id) Enroll
```
🟢 Only matching records.

### Left Outer Join (⟕)
```postgresql
Students ⟕ Enroll
```
Left side rows always retained (even without matches).

### Natural Join (⋈)
Matches automatically by same column name.
```postgresql
Students ⋈ Enroll
```
⚠️ Be careful — sometimes matches unintended columns.

### Division (÷) — Most abstract but powerful
Used when:

“Find all students who took ALL courses required in Program X.”

Example:
- Relation A(student, course)
- Relation B(course)
```postgresql
A ÷ B
```
📌 Output: all students who are enrolled in every course in B.

This operation is used in advanced query planning — SQL approximates using `GROUP BY HAVING COUNT`.

## 📌 Additional Operations
| Operation                  | Purpose                                                             | Example Use               |
| -------------------------- | ------------------------------------------------------------------- | ------------------------- |
| **Outer Union**            | Combine tables with different attributes (fills missing with NULLs) | When schemas differ       |
| **Aggregation & Grouping** | Mathematical/summary operations                                     | COUNT, SUM, AVG, MIN, MAX |

### Aggregation (F) and Group (γ)
Example: count students per city
```postgresql
γ city; COUNT(id) → num_students (Students)
```
Result:

| city       | num_students |
| ---------- | ------------ |
| Cairo      | 2            |
| Alexandria | 1            |

SQL Equivalent:
```postgresql
SELECT city, COUNT(id) FROM Students GROUP BY city;
```

## 💡 Mindset Shift
Relational Algebra is like pseudo-SQL for the brain:

You learn to think:
```postgresql
Filter → Select rows
Pick → Select fields
Combine → Join or Union
Summarize → Group + Aggregate
```
Then translate to SQL confidently.