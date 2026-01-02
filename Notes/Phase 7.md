# 🚀 Phase 7 — Normalization
Clean data → scalable systems  
Normalization = organizing data to reduce redundancy & avoid anomalies.

Phase 7 is where you learn how to design tables correctly so the database stays clean, efficient, and consistent—no duplicate data, no contradictions, no headaches.

## 💥 Why Normalize?
Poor database design leads to:

| Type of Anomaly    | Example                                                                       |
| ------------------ | ----------------------------------------------------------------------------- |
| **Update Anomaly** | Changing a customer’s phone in one row but forgetting others                  |
| **Insert Anomaly** | Unable to insert student data because they haven’t enrolled in any course yet |
| **Delete Anomaly** | Removing last purchase of a customer accidentally removes customer info       |


Normalization fixes these issues.

## 🔢 Normal Forms (NFs)
### 1️⃣ 1NF — First Normal Form
Rules:
- Each cell holds atomic (indivisible) values 
- No repeating groups or arrays in a field

❌ Bad:

| Student | Phones           |
| ------- | ---------------- |
| Ali     | 0101, 0122, 0155 |

✔️ Fixed:

| Student | Phone |
| ------- | ----- |
| Ali     | 0101  |
| Ali     | 0122  |
| Ali     | 0155  |

### 2️⃣ 2NF — Second Normal Form
Requirements:
- Must be in 1NF
- No partial dependency on a composite key
>Partial dependency = attribute depends only on part of a primary key

❌ Bad:
```postgresql
( student_id, course_id ) → grade
But student_name depends only on student_id.
```

✔️ Fix by splitting:
```postgresql
Students
(student_id → student_name)
```
```postgresql
Course Enrollments
(student_id, course_id → grade)
```

### 3️⃣ 3NF — Third Normal Form
Requirements:
- Must be in 2NF
- No transitive dependencies

>Transitive dependency: A → B → C  
⇒ C depends on A indirectly

❌ Bad:
```postgresql
student_id → department_id → department_name
```

✔️ Fix:
```postgresql
Students
(student_id, department_id)
```
```postgresql
Departments
(department_id, department_name)
```

## BCNF — Boyce-Codd Normal Form
A stricter version of 3NF.

>For every functional dependency A → B, A must be a candidate key.

Used when a non-key attribute can determine part of a key.

📌 Example:
- A professor can teach only one subject 
- Many professors can be in one department

But subject → professor (functional dependency), not aligned with the primary key → break into separate tables.

### 4️⃣ 4NF — Fourth Normal Form
Fixes multivalued dependencies (MVDs).

> MVD problem = when two attributes depend independently on same key, causing combinations to multiply.

❌ Bad:

| Student | Skill  | Language |
| ------- | ------ | -------- |
| Ali     | Excel  | English  |
| Ali     | Python | German   |

✔️ Split:
```postgresql
StudentSkills
(student, skill)
```
```postgresql
StudentLanguages
(student, language)
```

### 5️⃣ 5NF — Fifth Normal Form
Also called PJNF (Projection-Join NF).

Rules:
- Table cannot be split further without losing information. 
- Deals with join dependencies.

Used in complex many-to-many relationships involving junction tables.

📌 Example:  
A product may need multiple suppliers to supply multiple components → requires decomposition into multiple tables.

## 🧠 Other Concepts
### 📌 Multivalued Dependencies
Notation:
```postgresql
A ↠ B (A multi-determines B)
```
Occurs when attributes must exist in combinations; typically solved in 4NF by splitting tables.

### 📌 Inclusion Dependencies
Used to express that attributes in one relation must exist in another; similar to foreign keys.

Example:
```postgresql
Department.manager_id ⊆ Employees.employee_id
```
### 📌 Schema Decomposition
When breaking tables apart, ensure:

| Property                    | Meaning                                                    |
| --------------------------- | ---------------------------------------------------------- |
| **Lossless Join**           | No data is lost after splitting and joining back           |
| **Dependency Preservation** | All original functional dependencies are still enforceable |

⚠️ Always check these before finalizing a design.

### 📌 Algorithms for Schema Design
Design workflow:

1. Identify entities & relationships (ERD)
2. Find functional dependencies 
3. Choose candidate keys 
4. Normalize step-by-step (1NF → BCNF)
5. Check lossless-join & dependency preservation 
6. Implement schema in SQL

## 📌 Summary Table
| Normal Form | Fixes Problem                                  |
| ----------- | ---------------------------------------------- |
| **1NF**     | Repeating groups & non-atomic values           |
| **2NF**     | Partial dependencies                           |
| **3NF**     | Transitive dependencies                        |
| **BCNF**    | Non-key attribute determining key              |
| **4NF**     | Multivalued dependencies                       |
| **5NF**     | Join dependency; ensures minimal decomposition |