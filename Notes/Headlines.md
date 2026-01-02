# 🎯 Complete Database Learning Path
### From Fundamentals → Architect → Advanced Systems

## 📌 Phase 1 — Foundations of Databases

Core concepts every learner must master first

1. What is Data? What is a Database?
2. DBMS vs RDBMS vs Distributed SQL
3. Relational vs Non-Relational databases
4. Client-Server Architecture
5. Popular DBMS: MySQL, PostgreSQL, Oracle, SQL Server, MongoDB
6. OLTP vs OLAP (Basics)

## 📌 Phase 2 — Data Modeling & ERD
Learn how to design a database before writing SQL

1. Entity, Attribute, Tuple
2. Attribute types: simple, composite, multi-valued, stored/derived, complex, key
3. Keys: Primary, Foreign, Composite
4. Relationships: 1:1, 1:N, M:N; unary, binary, ternary
5. Optional vs Mandatory relationships
6. ERD (Entity-Relationship Diagrams)
7. EER — Enhanced Entity Relationship
   - Subclass, Superclass 
   - Specialization & Generalization 
     - Disjoint vs Overlapping 
     - Completeness constraints (Total / Partial)
   - Categories (UNION types)
   - Attribute & Relationship inheritance

## 📌 Phase 3 — Relational Model & Constraints
The theory behind SQL tables
    
- Relational Data Model concepts
- Integrity Constraints:
   - Key Constraints 
   - Entity Integrity 
   - Referential Integrity 
   - Domain Constraints 
- Solutions to violations (Cascade, Restrict, Null, Set Default)

## 📌 Phase 4 — Relational Algebra
Foundation of Query Optimization & SQL logic

- Unary: SELECT, PROJECT, RENAME
- Set Operations: UNION, INTERSECTION, DIFFERENCE, Cartesian Product
- Joins: Inner, Outer, Natural, Equi, Division
- Additional: Outer Union, Aggregation & Grouping

## 📌 Phase 4.5 — Data Types & Storage Formats
how values are represented.

- Data Types & Physical Representation
  - Numeric, fixed, floating, decimals
  - CHAR vs VARCHAR vs TEXT
  - BLOB / Binary data
  - JSON / XML columns
  - Collation & encoding (UTF-8 vs UTF-16)
  - Time zones & timestamp caveats

## 📌 Phase 5 — SQL Basics

Start coding using SQL

| Category                      | Topics                                                     |
| ----------------------------- | ---------------------------------------------------------- |
| DDL                           | CREATE, ALTER, DROP                                        |
| DML                           | INSERT, UPDATE, DELETE                                     |
| DQL                           | SELECT, WHERE, LIKE, IN, AND/OR                            |
| Sorting & Limits              | ORDER BY, LIMIT / TOP                                      |
| Constraints                   | NOT NULL, UNIQUE, PRIMARY KEY, FOREIGN KEY, CHECK, DEFAULT |
| Views                         | Creating & Using Views                                     |

## 📌 Phase 6 — Intermediate SQL

Business logic, nested queries, joins, aggregations

- Aggregate Functions: COUNT, SUM, AVG, MIN, MAX 
- GROUP BY & HAVING 
- Joins: INNER, LEFT, RIGHT, FULL 
- Subqueries: correlated vs non-correlated 
- Views & Materialized Views 
- Stored Procedures (Intro)
- SQL/PSM
- Function vs Procedure
- Triggers (Intro)
- Cursors (Intro)

## 📌 Phase 7 — Normalization

Clean data → scalable systems

| Normal Forms                       | Additional Concepts             |
| ---------------------------------- | ------------------------------- |
| 1NF, 2NF, 3NF, BCNF                | Multivalued Dependencies & 4NF  |
| 5NF & Join Dependency              | Inclusion Dependencies          |
| Anomalies (update, insert, delete) | Schema Decomposition properties |
| Algorithms for schema design       |                                 |

## 📌 Phase 8 — Indexing & Query Optimization

Performance tuning & scaling reads

### Indexing
- Types: B-Tree, Hash, Bitmap, Composite, Partial, Full-Text, Spatial
- Single level ordered indexes : Primary, Clustering, Secondary indexes 
- Dense vs Sparse indexing 
- Multi-level indexes, Fan-out, B+ Trees 
- Composite Index (Left-most Rule)
- Solve the 80/20 Performance Rule

### Query Optimization
- EXPLAIN PLAN, EXPLAIN ANALYZE 
- Cost-based optimization 
- Algorithms
  - Translating SQL Queries into Relational Algebra 
  - Algorithms for External Sorting 
  - Algorithms for SELECT and JOIN Operations 
  - Algorithms for PROJECT and SET Operations 
  - Implementing Aggregate Operations and Outer Joins
- Avoiding common slow patterns:
  - Avoid SELECT *
  - Avoid functions in WHERE 
  - Use IN instead of multiple OR
- Strategies
  - Using Heuristics in Query Optimization 
  - Using Selectivity and Cost Estimates in Query Optimization
  - Semantic Query Optimization 
  - Combining Operations Using Pipe lining

## 📌 Phase 9 — Transactions, Concurrency & Locking

Banking & mission-critical system fundamentals

### Core Transaction Fundamentals and Properties

- Single User System VS Multi user Systems
- ACID Properties 
- Transaction states, COMMIT/ROLLBACK/SAVEPOINT 
- Why Recovery is needed (Durability support) 
- Transaction Read and Write Operations 
- The system log (Undo/Redo Logs) 
- Transaction Support in SQL

### Concurrency Control and Isolation
- Why Concurrency Control is needed? 
  - dirty read problem 
  - The "incorrect summary" (or phantom read) problem 
  - unrepeatable read problem
- Isolation Levels:
  - Read Uncommitted
  - READ COMMITTED
  - REPEATABLE
  - Serializable 
  - Multiversion Concurrency Control (MVCC)
- MVCC, Undo/Redo Logs 
- Locks:
  - Shared(S), Exclusive(X) 
  - Intent Locks (IX, IS , SIX Modes)
  - Gap/Next-Key Locks
- Lock Management:
  - Lock Manager and Lock Conversion (upgrade/downgrade) 
  - Granularity of Data Items/Multiple Granularity locking
- Locking Protocols
  - 2-Phase Locking
  - Deadlocks & Avoidance
  - Deadlock and Starvation Problems
- Alternative Concurrency Control Protocols
  - Timestamp-Based Protocols
    - What is Transaction TimeStamp
    - Basic Timestamp Ordering Protocol 
    - Strict Timestamp Ordering Protocol 
    - Thoma's Write Rule
  - Timestamp & Validation Concurrency Control
- Schedules Analysis
  - Schedules and conflict operations 
  - Characterizing schedules based on Recoverability 
  - Characterizing Schedules Based on Serializability 
  - Recoverable Schedules 
  - Cascading Rollback and Cascadless schedule 
  - Strict Schedules


## 📌 Phase 10 — Storage Engines & Database Architecture

How data is physically stored

- Storage Hierarchy: Primary storage, secondary storage, and tertiary storage (Disk Storage)
- InnoDB vs MyISAM vs RocksDB vs Columnar engines 
- B-Tree vs LSM Tree 
- Pages, extents, segments 
- File Structures:
  - Operations on Files
  - Files of Unordered Records (Heap Files)
  - Files of Ordered Records (Sorted Files)
- Buffer Pool, Shared Memory, WAL 
- Hashing:
  - Internal vs External 
  - Collision handling (Open addressing, chaining)
  - Extendible, Linear, Dynamic Hashing 
- RAID & Disk I/O
- Persistent vs Transient Data

## 📌 Phase 11 — Stored Procedures, Triggers & Cursors

Server-side business logic & event-driven DB code 
- CREATE PROCEDURE 
- Functions vs Procedures 
- Triggers (BEFORE, AFTER)
- Auditing system example 
- When to avoid business logic in DB

## 📌 Phase 12 — Database Security

Protection, access control, SQL injection defense

- Fundamental Principles:
  - Rotate database credentials
  - No root connections from application
  - Privilege management (least privilege)
  - Introduction to Statistical Database Security
  - Introduction to Flow Control

- SQL Injection & Prepared Statements 
- GRANT / REVOKE 
- Role-based access control 
- Encryption basics 
- Sanitization & Validation

## 📌 Phase 13 — NoSQL & Modern Data Systems

When SQL is not enough

| Type        |        System | Use Case               |
|-------------| ------------: | ---------------------- |
| Key-Value   |         Redis | Caching / Sessions     |
| Document    |       MongoDB | Flexible schema        |
| Columnar    |     Cassandra | High write scalability |
| Graph       |         Neo4j | Social networks        |
| Search      | Elasticsearch | Full-text search       |


## 📌 Phase 14 — High Availability & Replication
Failover, scaling, distributed systems
- Master-Slave vs Multi-Master 
- Sharding 
- Read/Write Splitting 
- Heartbeat & Failover systems 
- Distributed SQL (CockroachDB, Yugabyte)

## 📌 Phase 15 — Data Warehousing & OLAP

Analytics, reporting & business intelligence

- Characteristics of Data Warehouses
- Building a Data Warehouse
- Typical Functionality of a Data Warehouse
- Problems and Open Issues in Data Warehouses
- Star Schema, Snowflake Schema 
- Fact vs Dimension Tables 
- Cubes: Rollup, Drilldown, Slice, Dice 
- ETL / ELT pipelines 
- Materialized Views

# 📌 Phase 16 — Database Recovery Techniques
Starts with core internals; ARIES is heavy so it comes after techniques.

- Recovery Concepts
- Recovery Techniques Based on Deferred Update 
- Recovery Techniques Based on Immediate Update 
- Shadow Paging 
- The ARIES Recovery Algorithm 
- Database Backup and Recovery from Catastrophic Failures 
- Recovery in Multidatabase Systems (after distributed later)


# 📌 Phase 17 — Distributed Databases and Client-Server Architectures
Builds on transactions & recovery, introduces distribution and coordination.

- Distributed Database Concepts 
- Data Fragmentation, Replication, and Allocation Techniques 
- Types of Distributed Database Systems 
- Query Processing in Distributed Databases 
- Overview of Concurrency Control & Recovery in Distributed DBs 
- An Overview of 3-Tier Client-Server Architecture 
- Distributed Databases in Oracle

# 📌 Phase 18 — Object-Relational and Extended-Relational Systems

After distributed systems; extends DBMS capabilities.

- Overview of SQL and Its Object-Relational Features 
- Evolution & Current Trends of Database Technology 
- Object-Relational Features of Oracle 8 
- The Informix Universal Server 
- Implementation Issues for Extended Type Systems 
- The Nested Relational Model

# 📌 Phase 19 — Object Database Standards, Languages, and Design
Natural follow-up to object-relational systems.

- Overview of the Object Model (ODMG)
- The Object Definition Language (ODL)
- The Object Query Language (OQL)
- Overview of C++ Language Binding 
- Object Database Conceptual Design


# 📌 Phase 20 — XML and Internet Databases

Prepares ground for semi-structured data trends.

- Structured, Semistructured, and Unstructured Data 
- XML Hierarchical (Tree) Data Model 
- XML Documents, DTD, and XML Schema 
- XML Documents and Databases 
- XML Querying (XPath, XQuery intro)

# 📌 Phase 21 — Data Mining Concepts
Bridges DB systems with analytics.

- Overview of Data Mining Technology 
- Association Rules 
- Classification 
- Clustering 
- Approaches to Other Data Mining Problems 
- Applications of Data Mining 
- Commercial Data Mining Tools

# 📌 Phase 22 — Emerging Database Technologies and Applications
Forward-looking final module.

- Mobile Databases
- Multimedia Databases 
- Geographic Information Systems 
- Genome Data Management
- GraphQL & Databases
    - GraphQL queries vs SQL queries
    - Federated data access
- Data Lake & Lakehouse Introduction
    - Contrast with Data Warehouse
    - Parquet, ORC, Avro formats
    - Delta Lake, Iceberg, Hudi (at conceptual level)
- Cloud Databases
- RDS / Aurora
- Spanner (as example of true distributed SQL)
