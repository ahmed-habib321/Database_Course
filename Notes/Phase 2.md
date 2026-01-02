# 🚀 Phase 2 — Data Modeling & ERD
Goal: Learn how to design a database before you write SQL.

## 1. Entity, Attribute, Tuple
| Term          | Definition                                   | Example (Student DB)           |
| ------------- | -------------------------------------------- | ------------------------------ |
| **Entity**    | A real-world object that we store data about | Student, Course, Teacher       |
| **Attribute** | A property/characteristic of an entity       | Student Name, Age, Email       |
| **Tuple**     | A single row/record in a table               | `("Ali", 22, "ali@email.com")` |

💡 In ERD (conceptual design), entities become tables and attributes become columns.

## 2. Attribute Types
| Type              | Meaning                                 | Example                            |
| ----------------- | --------------------------------------- | ---------------------------------- |
| **Simple**        | Cannot be divided further               | `Age`, `Email`                     |
| **Composite**     | Can be broken into sub-parts            | `Full Name → First + Last`         |
| **Multi-valued**  | Can have multiple values                | `Phone Numbers = {010..., 011...}` |
| **Stored**        | Directly stored in DB                   | `Date of Birth`                    |
| **Derived**       | Calculated from stored attributes       | `Age` (calculated from DOB)        |
| **Complex**       | Combination of composite + multi-valued | `Address {city, street, building}` |
| **Key Attribute** | Identifies each entity uniquely         | `Student_ID`                       |

## 3. Keys
| Key Type                | Purpose                                 |
| ----------------------- | --------------------------------------- |
| 🔑 **Primary Key (PK)** | Uniquely identifies a record in a table |
| 🔗 **Foreign Key (FK)** | Links two tables (relationship)         |
| 🧩 **Composite Key**    | PK made of **multiple attributes**      |

📌 Example
`Enrollment` table connecting `Students` & `Courses`:

| student_id *(FK)* | course_id *(FK)* | date_enrolled |
| ----------------- | ---------------- | ------------- |
| 10                | 5                | 2024-01-01    |

Primary key here can be: (student_id + course_id) → Composite Key

## 4. Relationship Types
### By Cardinality
| Type                   | Explanation                           | Example                       |
| ---------------------- | ------------------------------------- | ----------------------------- |
| **1:1 (One-to-One)**   | One record relates to only one record | Person ↔ Passport             |
| **1:N (One-to-Many)**  | One record connects to multiple       | Teacher → Students they teach |
| **M:N (Many-to-Many)** | Multiple on both sides                | Students ↔ Courses            |

📌 M:N relationships are always broken into a junction table in relational databases

### By Degree
| Degree                | Meaning                            | Example                            |
| --------------------- | ---------------------------------- | ---------------------------------- |
| **Unary (Recursive)** | Entity related to itself           | Employee manages Employee          |
| **Binary**            | Between two entities (most common) | Student ↔ Course                   |
| **Ternary**           | 3 entities involved                | Supplier supplies Product to Store |

## 5. Optional vs Mandatory Relationships
- Mandatory: Must have a related record 
  - Example: An Invoice must belong to a Customer
- Optional: Relationship is not required 
  - Example: A Customer may or may not have placed an order

📌 In ERDs, mandatory relationships are shown with bold or double lines.

## 6. ERD (Entity-Relationship Diagram)
A visual diagram representing:
- Entities (tables)
- Attributes (columns)
- Relationships (connections)

ERD Notations Used
- Chen Notation (conceptual)
- Crow’s Foot Notation (most used in practice)

Simple ERD (Crow’s Foot Example)
```postgresql
Student (PK student_id)  1 ────<  Enrollment  >────  N Course (PK course_id)
```
- Student and Course are entities 
- Enrollment is a linking/junction table for M:N

## 7. EER — Enhanced Entity Relationship
Used when modeling more complex real-world scenarios.

Superclass & Subclass
- Superclass = General entity 
- Subclass = More specific entities

📌 Example
```postgresql
Person (Superclass)
├── Student (Subclass)
└── Teacher (Subclass)
```
### Specialization & Generalization

| Term              | Direction                          | Example                   |
| ----------------- | ---------------------------------- | ------------------------- |
| 🎯 Specialization | One entity → multiple subtypes     | Person → Student, Teacher |
| 🌐 Generalization | Multiple entities → merge into one | Car, Truck → Vehicle      |

### Disjoint vs Overlapping Subclasses
| Type            | Meaning                                      | Example                                 |
| --------------- | -------------------------------------------- | --------------------------------------- |
| **Disjoint**    | Entity can belong to **only ONE** subclass   | Person is either **Student OR Teacher** |
| **Overlapping** | Entity can belong to **multiple** subclasses | Person can be **Student AND Teacher**   |

### Completeness Constraints
| Type        | Meaning                                      |
| ----------- | -------------------------------------------- |
| **Total**   | Every entity **must** belong to a subclass   |
| **Partial** | Some entities may not belong to any subclass |

📌 Example
- Total: Every person is either student or teacher
- Partial: Some persons are neither student nor teacher

### Categories (UNION Types)
When a subclass comes from multiple superclasses

Example:
```postgresql
Payment (Subclass)
├── CreditCard (Superclass)
└── BankTransfer (Superclass)
```
### Attribute & Relationship Inheritance
- Subclasses inherit attributes & relationships from the superclass 
- New attributes can be added to subclasses

📌 Example
```postgresql
Person (name, email)
├─ Student (grade_level)
└─ Teacher (salary)
```