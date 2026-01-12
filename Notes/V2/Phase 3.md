# 🗄️ Understanding the Relational Model & Database Constraints

This guide explains the fundamental concepts behind how SQL databases organize and protect data.

---

## 📊 What is the Relational Model?

The **Relational Model** is a framework for structuring data in databases. Think of it as the blueprint that defines how information should be organized, stored, and connected.

### Core Components

**Relations (Tables)**: The fundamental structure that holds data. Each relation represents an entity or concept in your database, like "Students" or "Courses."

**Tuples (Rows)**: Individual records within a table. Each tuple represents one instance of the entity—for example, one specific student.

**Attributes (Columns)**: The properties or characteristics that describe each entity. For a student, this might include name, ID, email, and enrollment date.

**Domains**: The set of valid values that each attribute can hold. This defines the data type and acceptable range—for instance, an age attribute might only accept positive integers between 0 and 120.

### Essential Keys

**Primary Key (PK)**: A special attribute (or combination of attributes) that uniquely identifies each tuple in a relation. No two rows can have the same primary key, and it can never be empty.

**Foreign Key (FK)**: An attribute in one table that creates a link to a primary key in another table. This is how relationships between different tables are established.

### The Goal

The relational model aims to organize data efficiently by eliminating redundancy, preventing errors, and making it easy to query and update information without compromising consistency.

---

## 🔒 Integrity Constraints: Keeping Data Valid

Integrity constraints are rules enforced by the database to ensure data remains accurate, consistent, and meaningful. They act as guardians against invalid data entry.

### 1. Key Constraints

These constraints ensure that records can be uniquely identified and distinguished from one another.

**Primary Key Constraint**:
- Every value must be unique across all rows
- Cannot contain NULL values
- Serves as the definitive identifier for each record

```sql
CREATE TABLE Students (
    student_id INT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);
```

**Unique Constraint**:
- Ensures no duplicate values in a column
- Unlike primary keys, unique constraints may allow NULL values (depending on the database system)
- Useful for attributes like email addresses or usernames

```sql
CREATE TABLE Users (
    user_id INT PRIMARY KEY,
    email VARCHAR(100) UNIQUE,
    username VARCHAR(50) UNIQUE
);
```

### 2. Entity Integrity

This principle states that **a primary key cannot be NULL**. The reasoning is straightforward: if you can't identify a record, it shouldn't exist in the table.

**Why it matters**: Without a valid primary key, you lose the ability to reference, update, or delete that specific record reliably.

❌ **Invalid**:
```sql
INSERT INTO Students (student_id, name) VALUES (NULL, 'Ahmed');
-- This violates entity integrity
```

✅ **Valid**:
```sql
CREATE TABLE Students (
    student_id INT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
```

### 3. Referential Integrity

This constraint ensures that **relationships between tables remain valid**. When one table references another through a foreign key, that reference must point to an existing record.

**The Rules**:
- A foreign key value must exist as a primary key in the referenced table
- Alternatively, the foreign key can be NULL (if the relationship is optional)

```sql
CREATE TABLE Enrollments (
    enrollment_id INT PRIMARY KEY,
    student_id INT,
    course_name VARCHAR(100),
    FOREIGN KEY (student_id) REFERENCES Students(student_id)
);
```

❌ **Invalid insertion**:
```sql
INSERT INTO Enrollments (enrollment_id, student_id, course_name)
VALUES (101, 999, 'Database Systems');
-- Fails if student_id 999 doesn't exist in Students table
```

**Why it matters**: Referential integrity prevents "orphaned" records—data that references non-existent entities, which would make the database inconsistent and unreliable.

### 4. Domain Constraints

Domain constraints ensure that each attribute contains only valid values according to its defined type and rules.

**Examples**:
- An `age` column defined as `INT` cannot accept text like "twenty"
- A `gender` column defined as `ENUM('M', 'F', 'Other')` must contain only those specified values
- An `email` column defined as `VARCHAR(100)` must be a string within 100 characters

```sql
CREATE TABLE Employees (
    emp_id INT PRIMARY KEY,
    age INT CHECK (age >= 18 AND age <= 65),
    salary DECIMAL(10,2) CHECK (salary > 0),
    hire_date DATE,
    department ENUM('HR', 'IT', 'Finance', 'Marketing')
);
```

**Additional rules** can be added using CHECK constraints to enforce business logic, such as ensuring salaries are positive or ages fall within realistic ranges.

---

## 📋 Constraints Summary Table

| Constraint Type | Purpose | Example |
|-----------------|---------|---------|
| **Key Constraint** | Ensures unique identification of records | `student_id INT PRIMARY KEY` |
| **Entity Integrity** | Prevents NULL values in primary keys | `PRIMARY KEY` columns cannot be NULL |
| **Referential Integrity** | Maintains valid relationships between tables | Foreign keys must reference existing primary keys |
| **Domain Constraint** | Enforces valid data types and value ranges | `age INT CHECK (age > 0)` |

---

## ⚠️ Handling Constraint Violations: Referential Actions

When data in a parent table changes (through updates or deletions), what happens to related records in child tables? Databases provide several strategies to handle these situations.

### Common Scenarios

Imagine you have a Students table and an Enrollments table. Each enrollment references a student. What happens if:
- A student is deleted from the Students table?
- A student's ID is updated?

### Referential Actions

**CASCADE**: Automatically propagate the change to all related records.
- If a parent record is deleted, all child records are deleted
- If a parent key is updated, all foreign keys in child tables are updated

```sql
FOREIGN KEY (student_id) REFERENCES Students(student_id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

**RESTRICT / NO ACTION**: Block the operation if it would break referential integrity.
- Prevents deletion or update of parent records if child records exist
- You must manually remove or update child records first

```sql
FOREIGN KEY (student_id) REFERENCES Students(student_id)
ON DELETE RESTRICT;
```

**SET NULL**: Replace the foreign key with NULL when the parent is deleted or updated.
- Child records remain in the table but lose their connection to the parent
- Only works if the foreign key column allows NULL values

```sql
FOREIGN KEY (student_id) REFERENCES Students(student_id)
ON DELETE SET NULL
ON UPDATE SET NULL;
```

**SET DEFAULT**: Replace the foreign key with a predefined default value.
- Requires that a default value is specified for the foreign key column
- Useful for maintaining a "fallback" relationship

```sql
FOREIGN KEY (student_id) REFERENCES Students(student_id)
ON DELETE SET DEFAULT;
```

---

## 🎯 Practical Example: Students & Enrollments

Let's see how these concepts work together with real data.

### Tables Setup

**Students Table**:
| student_id (PK) | name | email |
|-----------------|------|-------|
| 1 | Ahmed | ahmed@uni.edu |
| 2 | Sara | sara@uni.edu |
| 3 | Mohamed | mohamed@uni.edu |

**Enrollments Table**:
| enrollment_id (PK) | student_id (FK) | course_name | grade |
|--------------------|-----------------|-------------|-------|
| 101 | 1 | Database Systems | A |
| 102 | 1 | Data Structures | B+ |
| 103 | 2 | Web Development | A- |
| 104 | 3 | Algorithms | B |

### Scenario: Deleting a Student

```sql
DELETE FROM Students WHERE student_id = 1;
```

**What happens depends on the referential action**:

**With CASCADE**:
- Student Ahmed (ID = 1) is deleted
- Enrollments 101 and 102 are automatically deleted
- Result: Both the student and their enrollment history are removed

**With RESTRICT**:
- The delete operation is blocked
- Database returns an error: "Cannot delete student with existing enrollments"
- You must first delete enrollments 101 and 102 manually

**With SET NULL**:
- Student Ahmed is deleted
- Enrollments 101 and 102 remain in the table
- Their `student_id` column is set to NULL
- Result: Enrollment records exist but are no longer linked to any student

**With SET DEFAULT** (assuming default is 0):
- Student Ahmed is deleted
- Enrollments 101 and 102 remain
- Their `student_id` is changed to 0 (the default value)
- Requires a valid default student ID to exist

---

## ✅ Best Practices

1. **Always define primary keys** for every table to ensure records can be uniquely identified
2. **Use foreign keys** to enforce relationships and maintain referential integrity
3. **Choose appropriate referential actions** based on your business logic:
   - Use CASCADE when child records have no meaning without the parent
   - Use RESTRICT to prevent accidental data loss
   - Use SET NULL when relationships are optional
4. **Apply domain constraints** to validate data at the database level, not just in application code
5. **Document your constraints** so other developers understand the data rules