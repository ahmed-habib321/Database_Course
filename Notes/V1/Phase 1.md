# 🚀 Phase 1 — Foundations of Databases
Core concepts every learner must master first

## 1. What is Data? What is a Database?

### Data 
- Raw facts or details that have not yet been processed. 
- Can be numbers, text, images, audio, etc.

### Examples:
`"Ahmed"`, `25`, `"Cairo"`, `"Toyota Corolla"`

### Database
A database is a structured place/system where data is stored, managed, and retrieved efficiently.
- Think of it as a digital filing cabinet or Excel sheet — but much more powerful.

## 2. DBMS vs RDBMS vs Distributed SQL

| Term                | Meaning                                                                                         | Example                                 |
| ------------------- | ----------------------------------------------------------------------------------------------- | --------------------------------------- |
| **DBMS**            | Software that manages data in databases. Not necessarily structured with relationships.         | MS Access                               |
| **RDBMS**           | A DBMS based on **Relational (table-based)** model; supports relationships using keys.          | MySQL, PostgreSQL, Oracle               |
| **Distributed SQL** | Databases distributed across many servers for **high scalability**, while still supporting SQL. | CockroachDB, YugabyteDB, Google Spanner |

### Simple Visual
```postgresql
DBMS -> Stores data
RDBMS -> Stores data in related tables
Distributed SQL -> Stores data in multiple servers for speed & scale
```

## 3. Relational vs Non-Relational Databases

### Relational Databases
- Table-based (rows & columns)
- Use SQL 
- Good for structured data & complex queries

Examples: MySQL, PostgreSQL, SQL Server

📌 Example Table (Users)

| id | name   | age |
| -- | ------ | --- |
| 1  | Ahmed  | 25  |
| 2  | Mariam | 29  |

### Non-Relational Databases
- Also called NoSQL
- Flexible structure: documents, key-value, graph, etc.
- Good for fast development & large/varied data

Examples: MongoDB, Redis, Cassandra

📌 JSON-style Document (MongoDB)
```json
{
"name": "Ahmed",
"age": 25,
"hobbies": ["gaming", "reading"]
}
```

## 4. Client-Server Architecture

A model that describes how users (clients) interact with a server.

```sql
Client (user/app) ---> sends request ---> Server (DB)
Client <--- sends results back --- Server
```
- Client: Web app, mobile app, or program 
- Server: Database running on a machine responding to requests

💡 Example
When you log into Instagram:
- Client = the app 
- Server = the backend system + database that checks your username/password

## 5. Popular DBMS Systems
🔹 MySQL
- Open-source, widely used, easy to learn 
- Web applications (Facebook originally used it)

🔹 PostgreSQL
- More advanced features than MySQL 
- Handles complex queries excellently

🔹 Oracle
- Enterprise-grade, used by banks & large corporations 
- Very powerful but expensive

🔹 SQL Server
- From Microsoft, used in .NET environments

🔹 MongoDB
- Most popular NoSQL database 
- Stores JSON-like documents

## 6. OLTP vs OLAP (Basics)
| Type     | Purpose                        | Features                       | Example                     |
| -------- | ------------------------------ | ------------------------------ | --------------------------- |
| **OLTP** | Handle real-time transactions  | Fast inserts & updates         | Banking, E-commerce         |
| **OLAP** | Analytical queries & reporting | Heavy calculations; large data | Data warehouses, dashboards |

Visual Summary
```postgresql
   OLTP = Frontend (daily operations)
   OLAP = Backend (analysis & business insights)
```
📌 Example:
- OLTP: You purchase a product on Amazon → transaction stored
- OLAP: Amazon analyzes total sales for Black Friday → reports created