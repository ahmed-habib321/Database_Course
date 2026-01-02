# 🚀 Phase 18 — Object-Relational & Extended-Relational Systems
Extending DBMS capabilities beyond classic relational models

Phase 18 is about extending relational databases to handle more complex data types and richer applications, bridging the gap between traditional relational systems and modern object-oriented or multimedia applications. It builds naturally after distributed systems because complex applications often require advanced data types and modeling capabilities.

## 1️⃣ Overview of SQL & Object-Relational Features
Traditional SQL is relational and table-based. Object-relational systems add:
- User-defined types (UDTs) → e.g., complex numbers, geometric types 
- Inheritance of tables / types → reuse common attributes 
- Methods / Functions tied to types → encapsulate behavior 
- Collections & Arrays → nested structures inside columns 
- LOB (Large Object) support → images, videos, documents

Example:
```postgresql
CREATE TYPE Address_Type AS OBJECT (
street VARCHAR2(50),
city   VARCHAR2(30),
zip    VARCHAR2(10)
);
```
```postgresql
CREATE TABLE Customer (
id NUMBER PRIMARY KEY,
name VARCHAR2(50),
address Address_Type
);
```
## 2️⃣ Evolution & Current Trends of Database Technology
- Traditional Relational DBs → structured, normalized tables 
- Object-Relational DBs (ORDBMS) → relational + complex types 
- NoSQL & NewSQL → distributed, schema-flexible, highly scalable 
- Multi-model Databases → combine relational, document, graph, key-value

Trend: ORDBMS allow richer applications without abandoning SQL, bridging legacy relational systems with modern needs.

## 3️⃣ Object-Relational Features of Oracle 8
Oracle 8 introduced:
- Object types → encapsulate attributes and methods 
- Nested tables & varrays → store collections within a table 
- Inheritance → subtype tables derived from parent tables 
- Object views → expose relational tables as objects 
- LOB support → CLOB, BLOB for large objects

Example of nested table:
```postgresql
CREATE TYPE PhoneList AS TABLE OF VARCHAR2(20);
CREATE TABLE Employee (
emp_id NUMBER,
name   VARCHAR2(50),
phones PhoneList
) NESTED TABLE phones STORE AS phones_tab;
```

## 4️⃣ The Informix Universal Server
- Early commercial ORDBMS supporting:
  - Object types & collections 
  - Inheritance of types 
  - Integration with C++ / Java applications 
- Allowed rich applications like GIS, multimedia, and engineering data to be stored in a relational engine.

## 5️⃣ Implementation Issues for Extended Type Systems
Challenges in ORDBMS:
- Query optimization with nested / object types 
- Indexing complex data (arrays, LOBs, spatial types)
- Transaction & concurrency control with extended types 
- Storage layout & serialization of objects 
- Mapping objects to relational tables efficiently (object-relational impedance mismatch)

## 6️⃣ The Nested Relational Model
- Allows relations inside relations (tables inside tables)
- Useful for representing hierarchical or composite data 
- Example: a Customer table with a nested Orders table
```postgresql
Customer (cust_id, name, Orders)
Orders (order_id, date, amount)
```
- Benefits:
  - Natural representation for complex domains 
  - Reduces number of join operations for nested data

## 🎯 Phase 18 Key Takeaways
1. ORDBMS extend relational DBs to support objects, nested structures, and methods 
2. Enables storing complex types like multimedia, spatial, or hierarchical data 
3. Oracle 8 & Informix were pioneers in object-relational features 
4. Implementation challenges: indexing, storage, query optimization for objects 
5. Nested relational model allows relations inside relations, simplifying queries for complex domains 
6. ORDBMS is a bridge between classic SQL and modern application needs