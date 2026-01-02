# 🚀 Phase 3 — Relational Model & Constraints
The theory behind how SQL tables work and stay consistent.

## 📌 Relational Data Model Concepts
The Relational Model is a way of organizing data using:
- Relations (tables)
- Tuples (rows)
- Attributes (columns)
- Domains (data types for attributes)

### Key Rules/Concepts:
| Concept         | Meaning                                            |
| --------------- | -------------------------------------------------- |
| **Relation**    | A table with rows & columns                        |
| **Attribute**   | A column name; describes a property                |
| **Tuple**       | One data record / row                              |
| **Domain**      | Set of allowed values for a column                 |
| **Primary Key** | Unique identifier for each row                     |
| **Foreign Key** | Attribute that references a PK in another relation |

Goal: Organize data so it has no redundancy, avoids errors, and is easy to query and update.

## 📌 Integrity Constraints
Rules that ensure data is valid, consistent, and correct in the database.

### 1️⃣ Key Constraints
Primary Key (PK):
- Must be unique 
- Cannot be NULL 
- Identifies records uniquely
```postgresql
student_id INT PRIMARY KEY
```
Unique Constraint:
- Must be unique 
- Can be NULL (depending on DBMS)
```postgresql
email VARCHAR(50) UNIQUE
```
### 2️⃣ Entity Integrity
Ensures that Primary Key cannot be NULL.

Because if PK is null → you cannot identify the record.

❌ Invalid example:
```postgresql
student_id = NULL   -- breaks entity integrity
```
✔ Correct:
```postgresql
student_id NOT NULL PRIMARY KEY
```
### 3️⃣ Referential Integrity
Ensures Foreign Keys must reference valid Primary Keys in another table.

📌 FK value must:
- exist in the parent table 
- OR be NULL (if optional)

Example:
```postgresql
FOREIGN KEY (student_id) REFERENCES Students(student_id)
```
❌ You cannot insert:
```postgresql
student_id = 999  -- if 999 doesn't exist in Students table
```
### 4️⃣ Domain Constraints
Each attribute value must follow its defined data type & rules.

Examples:
- `age INT` → must be a number 
- `email VARCHAR(50)` → must be text 
- `gender ENUM('M','F')` → must be one of specified values

📌 These prevent incorrect data like:

- age = "abc"
- date = "tomorrow"
- salary = -300 (if rule: must be positive)

## 📍 Summary of Constraints
| Constraint            | Ensures                          |
| --------------------- | -------------------------------- |
| Key Constraint        | Uniqueness & identification      |
| Entity Integrity      | PK is never null                 |
| Referential Integrity | FK always points to valid record |
| Domain Constraint     | Attribute values follow rules    |

## 📌 What Happens When Constraints are Violated?
Databases provide actions to handle situations when referenced data changes.

### 💥 Scenario:
- A parent record is deleted or updated
- This affects child records referencing it

### Solutions (FK Actions)
| Action                   | Meaning                                  | Example Effect                        |
| ------------------------ | ---------------------------------------- | ------------------------------------- |
| **CASCADE**              | Automatically apply same change to child | If parent deleted → child deleted     |
| **RESTRICT / NO ACTION** | Prevent change                           | Delete blocked until children removed |
| **SET NULL**             | FK becomes NULL                          | Child stays but loses the link        |
| **SET DEFAULT**          | FK replaced with a default value         | Requires default defined              |

### 📌 Example Foreign Key with these rules:
```postgresql
FOREIGN KEY (student_id)
REFERENCES Students(student_id)
ON DELETE CASCADE
ON UPDATE SET NULL;
```
Meaning:
- If a student is deleted → their enrollments are also deleted 
- If student_id changes → child tables set FK to NULL

### 📌 Quick Real-Life Example
Students & Courses

Students Table

| student_id (PK) | name  |
| --------------- | ----- |
| 1               | Ahmed |
| 2               | Sara  |

Enrollments Table

| enr_id | student_id (FK) | course |
| ------ | --------------- | ------ |
| 10     | 1               | Math   |
| 11     | 2               | CS     |

If we do:
```postgresql
DELETE FROM Students WHERE student_id = 1;
```
- With CASCADE → Enr_id 10 deleted too
- With RESTRICT → SQL error; delete blocked
- With SET NULL → student_id in enr_id 10 becomes NULL
- With SET DEFAULT → student_id resets to a default (like 0 or -1)