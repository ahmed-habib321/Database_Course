# Data Modeling & ERD - Phase 2 Study Guide

## Introduction to Data Modeling

Before writing a single line of SQL code, successful database developers create a blueprint of their database structure. This design phase, called data modeling, helps you visualize how data relates, prevents costly mistakes, and ensures your database can grow with your application's needs.

---

## Core Database Concepts

### Entity, Attribute, and Tuple

Understanding these three fundamental concepts is essential for database design:

**Entity**

An entity represents any real-world object or concept about which you want to store information. Entities become tables in your database.

Think of entities as the "nouns" of your database. If you were building a university database, you'd ask yourself: "What things do I need to track?" The answers become your entities.

**Examples:**
- Student (a person enrolled in courses)
- Course (a class being offered)
- Teacher (an instructor)
- Department (an academic division)
- Textbook (learning materials)

**Attribute**

An attribute describes a specific characteristic or property of an entity. Attributes become columns in your database tables.

These are the details you need to know about each entity. For a Student entity, you'd ask: "What information do I need to store about each student?"

**Examples for Student entity:**
- Student_ID
- First_Name
- Last_Name
- Email
- Date_of_Birth
- Major
- GPA

**Tuple**

A tuple is a single, complete record in your database - one row in a table representing a specific instance of an entity.

**Example tuple in Students table:**
```
(12345, "Ali", "Hassan", "ali.hassan@email.com", "2002-03-15", "Computer Science", 3.7)
```

**The Connection:**

When you design a database, you follow this transformation:
1. Identify entities (real-world objects) → These become **tables**
2. Define attributes (properties) → These become **columns**
3. Store tuples (actual data) → These become **rows**

---

## Understanding Attribute Types

Not all attributes are created equal. Different types serve different purposes in your database design.

### Simple Attributes

Simple attributes are atomic - they cannot be meaningfully divided into smaller parts. They represent single, indivisible values.

**Examples:**
- Age: 22
- Email: student@university.edu
- GPA: 3.8
- Status: "Active"

These are stored directly in your database as single values in single columns.

### Composite Attributes

Composite attributes can be broken down into smaller, meaningful sub-parts. While you could store them as one value, separating them provides more flexibility.

**Example - Full Name:**
- Instead of: "Ahmed Mohamed Hassan"
- Break into: First_Name: "Ahmed", Middle_Name: "Mohamed", Last_Name: "Hassan"

**Why this matters:** Separating names allows you to sort by last name, address people by first name, or generate formal documents using full names.

**Example - Address:**
- Instead of: "123 Main Street, Apartment 4B, Cairo, Egypt"
- Break into: Street_Number: "123", Street_Name: "Main Street", Apartment: "4B", City: "Cairo", Country: "Egypt"

### Multi-valued Attributes

Some attributes naturally have multiple values for a single entity. A person might have multiple phone numbers or speak several languages.

**Examples:**
- Phone_Numbers: {01012345678, 01198765432}
- Languages_Spoken: {"Arabic", "English", "French"}
- Previous_Universities: {"Cairo University", "Alexandria University"}

**Database Implementation:** Multi-valued attributes typically require a separate table to properly store multiple values while maintaining database normalization.

### Stored Attributes

Stored attributes contain data that you directly input and save in the database. They're the raw facts you collect.

**Examples:**
- Date_of_Birth: "2000-05-20"
- Enrollment_Date: "2023-09-01"
- Salary: 50000
- Product_Price: 299.99

### Derived Attributes

Derived attributes are calculated from other stored attributes rather than being stored themselves. They provide useful information without consuming storage space.

**Examples:**
- Age (derived from Date_of_Birth and current date)
- Years_of_Service (derived from Hire_Date)
- Total_Price (derived from Unit_Price × Quantity)
- Grade_Average (derived from individual course grades)

**Design Decision:** Whether to store or derive an attribute depends on how frequently you need it versus the cost of recalculating it each time.

### Complex Attributes

Complex attributes combine characteristics of both composite and multi-valued attributes. They're nested structures containing multiple sub-parts that can themselves have multiple values.

**Example - Contact Information:**
```
Contact_Info {
  Phones [
    {Type: "Mobile", Number: "01012345678"},
    {Type: "Home", Number: "023456789"}
  ],
  Addresses [
    {Type: "Home", Street: "Main St", City: "Cairo"},
    {Type: "Work", Street: "Commerce Blvd", City: "Giza"}
  ]
}
```

### Key Attributes

Key attributes uniquely identify each entity instance. No two entities can have the same key attribute value.

**Examples:**
- Student_ID: Each student gets a unique identifier
- Social_Security_Number: Unique per person
- ISBN: Unique per book
- License_Plate: Unique per vehicle

**Critical property:** Key attributes must never be NULL and must never change once assigned.

---

## Keys: The Foundation of Database Relationships

Keys are special attributes that establish identity and create connections between tables. Understanding keys is crucial for proper database design.

### Primary Key (PK)

The primary key is the attribute (or combination of attributes) that uniquely identifies each record in a table. Every table must have exactly one primary key.

**Characteristics:**
- Must be unique for every record
- Cannot contain NULL values
- Should never change once assigned
- Preferably short and simple

**Example - Students Table:**
```
Student_ID (PK) | First_Name | Last_Name | Email
----------------+------------+-----------+-------------------
S001            | Ahmed      | Ali       | ahmed@uni.edu
S002            | Fatima     | Hassan    | fatima@uni.edu
S003            | Mohamed    | Youssef   | mohamed@uni.edu
```

**Choosing a Primary Key:**
- Natural keys: Use existing meaningful data (like ISBN for books)
- Surrogate keys: Create artificial identifiers (like auto-incrementing IDs)

Most modern databases favor surrogate keys because they're simple, never change, and have no business meaning that might evolve.

### Foreign Key (FK)

A foreign key is an attribute in one table that references the primary key of another table. Foreign keys create relationships between tables, allowing you to connect related data.

**Example:**

**Students Table:**
```
Student_ID (PK) | Name   | Major_ID (FK)
----------------+--------+--------------
S001            | Ahmed  | M10
S002            | Fatima | M20
```

**Majors Table:**
```
Major_ID (PK) | Major_Name
--------------+--------------------
M10           | Computer Science
M20           | Business Administration
```

The Major_ID in the Students table is a foreign key that references the primary key in the Majors table, establishing which major each student belongs to.

**Foreign Key Rules:**
- The value must exist in the referenced table (referential integrity)
- Can be NULL if the relationship is optional
- Multiple rows can have the same foreign key value

### Composite Key

A composite key consists of two or more attributes that together uniquely identify a record. While each attribute alone might not be unique, their combination is.

**When to use composite keys:**
- For junction tables in many-to-many relationships
- When no single attribute can serve as a unique identifier
- When the combination has business meaning

**Example - Enrollment Table:**
```
Student_ID (PK, FK) | Course_ID (PK, FK) | Semester   | Grade
--------------------+--------------------+------------+-------
S001                | CS101              | Fall 2023  | A
S001                | CS102              | Fall 2023  | B+
S002                | CS101              | Fall 2023  | A-
```

Here, the composite primary key is (Student_ID, Course_ID) because:
- A student can enroll in multiple courses
- A course can have multiple students
- But each student-course combination should appear only once per semester

---

## Relationship Types by Cardinality

Cardinality describes how many instances of one entity can be associated with instances of another entity. Getting cardinality right is crucial for proper database design.

### One-to-One (1:1)

In a one-to-one relationship, each instance of Entity A relates to exactly one instance of Entity B, and vice versa.

**Example - Person and Passport:**
- Each person has exactly one passport
- Each passport belongs to exactly one person

**Database Implementation:**
```
Person Table:
Person_ID (PK) | Name   | Birth_Date
---------------+--------+------------
P001           | Ahmed  | 1990-05-15

Passport Table:
Passport_Number (PK) | Person_ID (FK) | Issue_Date | Expiry_Date
---------------------+----------------+------------+-------------
A12345678            | P001           | 2020-01-10 | 2030-01-10
```

**When to use 1:1:**
- Splitting tables for performance (separating frequently accessed from rarely accessed data)
- Security reasons (keeping sensitive information in a separate table)
- Optional information (not all entities have the related data)

### One-to-Many (1:N)

In a one-to-many relationship, each instance of Entity A can relate to multiple instances of Entity B, but each instance of Entity B relates to only one instance of Entity A.

This is the most common relationship type in databases.

**Example - Teacher and Students:**
- One teacher instructs many students
- Each student has one primary academic advisor (teacher)

**Database Implementation:**
```
Teachers Table:
Teacher_ID (PK) | Name           | Department
----------------+----------------+-------------
T001            | Dr. Mohamed    | CS
T002            | Dr. Sara       | Mathematics

Students Table:
Student_ID (PK) | Name    | Advisor_ID (FK)
----------------+---------+-----------------
S001            | Ahmed   | T001
S002            | Fatima  | T001
S003            | Hassan  | T002
```

The foreign key goes in the "many" side (Students table), pointing to the "one" side (Teachers table).

**Common Examples:**
- Department → Employees
- Customer → Orders
- Author → Books (if considering primary author)
- Category → Products

### Many-to-Many (M:N)

In a many-to-many relationship, multiple instances of Entity A can relate to multiple instances of Entity B, and vice versa.

**Example - Students and Courses:**
- One student enrolls in many courses
- One course has many students enrolled

**The Problem:** Relational databases cannot directly implement many-to-many relationships.

**The Solution:** Create a junction table (also called bridge table, linking table, or associative entity) that breaks the M:N relationship into two 1:N relationships.

**Database Implementation:**
```
Students Table:
Student_ID (PK) | Name
----------------+--------
S001            | Ahmed
S002            | Fatima

Courses Table:
Course_ID (PK) | Course_Name       | Credits
---------------+-------------------+---------
CS101          | Programming       | 3
CS102          | Data Structures   | 4
MATH201        | Calculus          | 3

Enrollments Table (Junction):
Enrollment_ID (PK) | Student_ID (FK) | Course_ID (FK) | Semester   | Grade
-------------------+-----------------+----------------+------------+-------
E001               | S001            | CS101          | Fall 2023  | A
E002               | S001            | CS102          | Fall 2023  | B+
E003               | S002            | CS101          | Fall 2023  | A-
E004               | S002            | MATH201        | Fall 2023  | B
```

**Why Junction Tables are Necessary:**
- They resolve the M:N relationship into manageable 1:N relationships
- They can store attributes specific to the relationship (like Grade and Semester)
- They maintain data integrity and avoid redundancy

---

## Relationship Types by Degree

Degree refers to the number of entity types participating in a relationship.

### Unary (Recursive) Relationships

A unary relationship connects an entity to itself. This models hierarchical or network structures within a single entity type.

**Example - Employee Management Structure:**
```
Employee Table:
Employee_ID (PK) | Name           | Manager_ID (FK)
-----------------+----------------+-----------------
E001             | Ahmed (CEO)    | NULL
E002             | Sara           | E001
E003             | Mohamed        | E001
E004             | Fatima         | E002
```

Each employee has a manager who is also an employee. The CEO has no manager (NULL).

**Other Examples:**
- Social media friends (User ↔ User)
- Course prerequisites (Course → Course)
- Product recommendations (Product → Product)
- Family relationships (Person → Person)

### Binary Relationships

Binary relationships involve two different entity types. This is by far the most common relationship degree in database design.

**Examples:**
- Student → Course
- Customer → Order
- Employee → Department
- Book → Publisher

Nearly all the relationships you'll design will be binary relationships.

### Ternary Relationships

Ternary relationships involve three entity types simultaneously. These are less common and more complex to model.

**Example - Supply Chain:**
```
Supplier supplies Product to Store
```

A specific ternary relationship might be: "Supplier ABC supplies Product XYZ to Store Location 123"

**Database Implementation:**
```
Supply Table:
Supply_ID (PK) | Supplier_ID (FK) | Product_ID (FK) | Store_ID (FK) | Quantity | Date
---------------+------------------+-----------------+---------------+----------+------------
SUP001         | SUP100           | PROD500         | STORE10       | 1000     | 2024-01-15
```

**When to use ternary:**
- When the relationship cannot be broken down into binary relationships without losing meaning
- When attributes depend on all three entities together

**Important:** Most apparent ternary relationships can actually be decomposed into binary relationships with a central entity, which is often simpler to implement and understand.

---

## Optional vs Mandatory Relationships

Participation constraints define whether an entity must participate in a relationship or if participation is optional.

### Mandatory Relationships (Total Participation)

In a mandatory relationship, every instance of the entity must be related to at least one instance of the other entity.

**Examples:**

**Invoice → Customer (Mandatory)**
- Every invoice must belong to a customer
- You cannot have an invoice without knowing who it's for
- In the database: Customer_ID in Invoice table cannot be NULL

**Employee → Department (Mandatory)**
- Every employee must be assigned to a department
- No employee can exist without department assignment
- In the database: Department_ID in Employee table cannot be NULL

**Database Implementation:** Use NOT NULL constraint on the foreign key column.

```sql
CREATE TABLE Invoices (
  Invoice_ID INT PRIMARY KEY,
  Customer_ID INT NOT NULL,  -- Mandatory relationship
  FOREIGN KEY (Customer_ID) REFERENCES Customers(Customer_ID)
);
```

### Optional Relationships (Partial Participation)

In an optional relationship, an entity instance may or may not be related to the other entity.

**Examples:**

**Customer → Orders (Optional)**
- A customer may exist in the system without having placed any orders yet
- New customers or inactive customers might have zero orders
- Some customers might have many orders

**Employee → Spouse (Optional)**
- Not all employees are married
- The relationship exists for some employees but not others

**Database Implementation:** Allow NULL values in the foreign key column.

```sql
CREATE TABLE Employees (
  Employee_ID INT PRIMARY KEY,
  Name VARCHAR(100),
  Spouse_ID INT NULL,  -- Optional relationship
  FOREIGN KEY (Spouse_ID) REFERENCES Persons(Person_ID)
);
```

### Visualization in ERD

In Entity-Relationship Diagrams:
- **Mandatory relationships:** Shown with double lines or thick lines
- **Optional relationships:** Shown with single lines

Understanding participation constraints helps you:
- Write proper validation rules
- Set appropriate database constraints
- Avoid data integrity issues
- Design better user interfaces

---

## Entity-Relationship Diagrams (ERD)

An ERD is a visual representation of your database design showing entities, attributes, and relationships. Creating an ERD before coding prevents costly mistakes and communicates your design to others.

### What ERDs Show

**Entities:** Represented as rectangles, these become your database tables

**Attributes:** Listed inside or beside entities, these become your table columns

**Relationships:** Shown as lines connecting entities, these indicate how tables relate

**Cardinality:** Symbols on relationship lines show whether relationships are 1:1, 1:N, or M:N

### Common ERD Notations

**Chen Notation**
- Academic and conceptual
- Uses diamonds for relationships
- Good for learning database concepts

**Crow's Foot Notation**
- Industry standard
- More practical for implementation
- Lines show cardinality with symbols resembling a crow's foot

**Crow's Foot Symbols:**
- `|` (one): Exactly one
- `○` (zero): Zero or optional
- `<` (many): Many
- `||` : One and only one (mandatory)
- `○<` : Zero or many (optional, many)
- `|<` : One or many (mandatory, many)

### Simple ERD Example (Crow's Foot)

```
Student ||--------○< Enrollment >○--------|| Course
```

**Reading this diagram:**
- Each Student can have zero or many Enrollments
- Each Course can have zero or many Enrollments
- Each Enrollment belongs to exactly one Student and exactly one Course
- Enrollment is a junction table resolving the M:N relationship

### Practical ERD Example

**University System:**

```
Department ||--------○< Employee
    |
    ||--------○< Course
    
Course ||--------○< Section
Section ||--------○< Enrollment >○--------|| Student
```

**This shows:**
- Departments have many Employees (1:N)
- Departments offer many Courses (1:N)
- Courses have many Sections (1:N)
- Students enroll in Sections through Enrollment junction table (M:N)

---

## Enhanced Entity-Relationship (EER) Diagrams

When modeling complex real-world scenarios, basic ERD notation isn't always sufficient. EER extends traditional ERD with additional concepts for inheritance, categorization, and specialization.

### Superclass and Subclass Hierarchy

Real-world entities often have hierarchical relationships where more specific types inherit characteristics from general types.

**Superclass:** A general entity containing common attributes

**Subclass:** A specialized entity inheriting from the superclass and adding specific attributes

**Example - Person Hierarchy:**
```
Person (Superclass)
  ├── Student (Subclass)
  ├── Teacher (Subclass)
  └── Staff (Subclass)
```

**Person attributes:** Person_ID, Name, Date_of_Birth, Email, Phone

**Additional Student attributes:** Student_ID, Major, GPA, Enrollment_Date

**Additional Teacher attributes:** Employee_ID, Department, Salary, Hire_Date

**Additional Staff attributes:** Employee_ID, Position, Department, Salary

### Specialization vs Generalization

These are two approaches to creating superclass-subclass hierarchies, representing opposite design directions.

**Specialization (Top-Down Approach)**

You start with a general entity and identify specialized subtypes with unique characteristics.

**Process:**
1. Begin with a broad entity (Person)
2. Identify distinct subtypes (Student, Teacher, Staff)
3. Create subclasses for each subtype
4. Move type-specific attributes to appropriate subclasses

**Example:**
```
Vehicle (Superclass)
  ├── Car (has: number_of_doors, trunk_capacity)
  ├── Truck (has: cargo_capacity, number_of_axles)
  └── Motorcycle (has: engine_displacement, has_sidecar)
```

**When to use:** When designing from scratch or when you recognize patterns in an existing broad entity

**Generalization (Bottom-Up Approach)**

You start with multiple specific entities and identify common characteristics to create a general superclass.

**Process:**
1. Begin with multiple specific entities (Car, Truck, Motorcycle)
2. Identify common attributes (license_plate, manufacturer, year, color)
3. Create a superclass (Vehicle)
4. Move common attributes to the superclass

**Example:**
```
Multiple entities: SavingsAccount, CheckingAccount, CreditCard
Common attributes: account_number, balance, owner, opening_date
Result: BankAccount (Superclass)
  ├── SavingsAccount (adds: interest_rate)
  ├── CheckingAccount (adds: overdraft_limit)
  └── CreditCard (adds: credit_limit, apr)
```

**When to use:** When refactoring an existing database or when combining similar entities

### Disjoint vs Overlapping Subclasses

This constraint determines whether an entity can belong to multiple subclasses simultaneously.

**Disjoint Subclasses (Exclusive)**

An entity instance can belong to only ONE subclass. This represents mutually exclusive categories.

**Symbol in ERD:** `d` inside a circle connecting subclasses

**Example - Person as Student OR Teacher:**
```
Person
  ├── Student  } Disjoint
  └── Teacher  } (d)
```

A person is either a student or a teacher, but not both.

**Real-world examples:**
- Payment type: CreditCard OR DebitCard OR Cash (one payment method per transaction)
- Employee type: FullTime OR PartTime OR Contractor (one employment status)
- Account status: Active OR Suspended OR Closed (one status at a time)

**Overlapping Subclasses (Inclusive)**

An entity instance can belong to multiple subclasses simultaneously. This represents non-exclusive categories.

**Symbol in ERD:** `o` inside a circle connecting subclasses

**Example - Person as Student AND Teacher:**
```
Person
  ├── Student  } Overlapping
  └── Teacher  } (o)
```

A person can be both a student (taking courses) and a teacher (teaching courses) simultaneously, such as a graduate teaching assistant.

**Real-world examples:**
- Person: Author AND Reviewer (same person can write papers and review others' papers)
- Employee: Manager AND Engineer (technical manager who still codes)
- Product: Discounted AND Featured (can have multiple promotional categories)

### Completeness Constraints

These constraints determine whether every superclass instance must belong to at least one subclass.

**Total Completeness (Mandatory Participation)**

Every instance of the superclass must belong to at least one subclass. No superclass instance can exist without being categorized.

**Symbol in ERD:** Double line from superclass to specialization circle

**Example - Total Person Classification:**
```
Person (every person must be categorized)
  ├── Student
  ├── Teacher
  └── Staff
```

If someone is in the Person table, they must also be classified as Student, Teacher, or Staff.

**When to use:**
- When every entity must have a specific type
- When the superclass has no meaning without specialization
- When all possible subtypes are known and accounted for

**Partial Completeness (Optional Participation)**

Some instances of the superclass may not belong to any subclass. Entities can exist in the superclass without specialization.

**Symbol in ERD:** Single line from superclass to specialization circle

**Example - Partial Person Classification:**
```
Person (some people may not be categorized)
  ├── Student
  └── Teacher
```

The database can contain people who are neither students nor teachers (perhaps alumni, applicants, or visitors).

**When to use:**
- When some entities don't fit into any specialized category
- When new subtypes might be added later
- When the superclass has meaning independent of its subclasses

### Combining Constraints

You can combine disjoint/overlapping with total/partial to create four possible combinations:

**1. Disjoint + Total:** Every entity belongs to exactly one subclass
- Example: Every vehicle is exclusively either a Car, Truck, or Motorcycle

**2. Disjoint + Partial:** Entities belong to at most one subclass, but some may belong to none
- Example: Persons can be Students or Teachers (but not both), or neither

**3. Overlapping + Total:** Every entity belongs to one or more subclasses
- Example: Every employee must have at least one role: Manager, Engineer, or both

**4. Overlapping + Partial:** Entities can belong to multiple, one, or no subclasses
- Example: Products can be Featured and/or Discounted, or neither

### Categories (Union Types)

Categories represent a subclass that can be formed from the union of multiple different superclasses. Unlike standard inheritance where a subclass inherits from one superclass, a category inherits from multiple superclasses.

**Key Characteristic:** A category instance belongs to only ONE of its superclasses at a time, but which superclass it belongs to can vary between instances.

**Example - Payment System:**
```
Payment (Category/Subclass)
  ├── CreditCard (Superclass)
  ├── BankTransfer (Superclass)
  └── DigitalWallet (Superclass)
```

**Explanation:**
- Each payment is associated with exactly one payment method
- Payment #1 might be from a CreditCard
- Payment #2 might be from a BankTransfer
- Payment #3 might be from a DigitalWallet
- But each individual payment connects to only one superclass

**Another Example - Registered User:**
```
RegisteredUser (Category)
  ├── Person (Superclass)
  └── Company (Superclass)
```

A registered user account can belong to either a Person or a Company, but not both. The RegisteredUser category allows you to treat both types uniformly for features like login, permissions, and billing.

**When to use categories:**
- When you need to treat different types of entities uniformly
- When entities share common operations but have different structures
- When implementing polymorphic relationships

### Attribute and Relationship Inheritance

Inheritance is the mechanism by which subclasses automatically acquire attributes and relationships from their superclass.

**Attribute Inheritance:**

When you create a subclass, it inherits all attributes from its superclass without needing to redefine them.

**Example:**
```
Person (Superclass)
  - Person_ID
  - Name
  - Date_of_Birth
  - Email
  - Phone

Student (Subclass) inherits all Person attributes, plus adds:
  - Student_ID
  - Major
  - GPA
  - Enrollment_Date

Teacher (Subclass) inherits all Person attributes, plus adds:
  - Employee_ID
  - Department
  - Salary
  - Hire_Date
```

**What this means:**
- Every Student has Person_ID, Name, Date_of_Birth, Email, Phone (inherited)
- Every Student also has Student_ID, Major, GPA, Enrollment_Date (specific)
- The database doesn't duplicate Person attributes in the Student table
- Queries can access both inherited and specific attributes

**Relationship Inheritance:**

Subclasses also inherit relationships from their superclass.

**Example:**
```
Person has relationship with Address (1:N - one person can have multiple addresses)

When Student inherits from Person:
  → Student automatically inherits the relationship with Address
  → No need to redefine how Students relate to Addresses

When Teacher inherits from Person:
  → Teacher also inherits the relationship with Address
  → Teachers and Students both have addresses through inheritance
```

**Benefits of Inheritance:**
- **Eliminates redundancy:** Common attributes defined once in superclass
- **Maintains consistency:** Changes to superclass automatically apply to all subclasses
- **Simplifies maintenance:** Update one place instead of multiple tables
- **Reflects reality:** Models real-world "is-a" relationships naturally

**Database Implementation Note:**

There are multiple ways to implement inheritance in physical databases:
- Single table (all attributes in one table with a type discriminator)
- Table per class (separate table for each class)
- Table per subclass (superclass table + additional tables for subclasses)

The choice depends on your specific requirements for performance, flexibility, and complexity.

---

## Practical Tips for Data Modeling

**Start Simple:** Begin with basic entities and relationships, then add complexity as needed

**Validate with Stakeholders:** Review your ERD with users and developers to catch issues early

**Normalize Appropriately:** Balance between normalization and practical query performance

**Document Assumptions:** Note business rules and constraints that influence your design

**Think About Growth:** Design for future expansion of features and data volume

**Use Meaningful Names:** Choose clear, consistent names for entities, attributes, and relationships

**Review Examples:** Study ERDs from similar systems to learn best practices