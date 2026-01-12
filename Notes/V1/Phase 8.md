# 🚀 Phase 8 — Indexing & Query Optimization
Performance tuning & scaling reads

Phase 8 is where you level up from “writing SQL” to engineering performance.
Databases at scale are fast only if designed to be.
This phase teaches how indexing and query optimization make massive data feel light.

## 🧱 INDEXING
Indexes are like a book’s table of contents — they speed up data lookup by avoiding full scans.

### 📌 Types of Indexes
| Type                  | Best For                                    | Notes                                |
| --------------------- | ------------------------------------------- | ------------------------------------ |
| **B-Tree (B+ Trees)** | Most queries (default)                      | Balanced, good for ranges            |
| **Hash Index**        | Exact match `=` lookups                     | Bad for ranges (`<`, `>`, `BETWEEN`) |
| **Bitmap Index**      | Low-cardinality columns (few unique values) | Data warehouses; OLAP                |
| **Composite Index**   | Multiple columns                            | Follow *Left-most Rule*              |
| **Partial Index**     | Conditions applied on subset of data        | `WHERE status='active'`              |
| **Full-Text Index**   | Searching long text/documents               | `MATCH AGAINST`, `to_tsvector`       |
| **Spatial Index**     | Geo/spatial data                            | GIS, maps, location queries          |

### 📌 Single-Level Ordered Indexes
| Type                 | Description                                |
| -------------------- | ------------------------------------------ |
| **Primary Index**    | Automatic on primary key                   |
| **Clustering Index** | Controls physical ordering of rows on disk |
| **Secondary Index**  | Additional, non-ordering index             |

🔑 A table can have one clustering index, multiple secondary indexes.

### 📌 Dense vs Sparse Indexes
| Dense                                    | Sparse                          |
| ---------------------------------------- | ------------------------------- |
| Every record in table has entry in index | One entry per disk block (page) |
| Faster lookups                           | Smaller, less memory            |
| Used more in secondary indexes           | Used in clustering indexes      |

### 📌 Multi-Level Index (B+ Trees)
For large data, index is broken into levels: Root ➝ Internal nodes ➝ Leaves.
- Fan-out = number of pointers per node 
- High fan-out = fewer levels = faster reads 
- Search time ≈ O(log n)
```postgresql
       [Root]
    /     |    \
  [N1]   [N2]   [N3]
   |       |      |
[Leaves with actual row pointers]
```
### 📌 Composite Index + Left-Most Rule
Order matters!
```postgresql
CREATE INDEX idx_user_city_age ON users(city, age);
```
This index can support:

| Query                               | Uses Index?                  |
| ----------------------------------- | ---------------------------- |
| `WHERE city = 'Cairo'`              | ✔️ Yes                       |
| `WHERE city = 'Cairo' AND age = 30` | ✔️ Yes                       |
| `WHERE age = 30`                    | ❌ No (breaks left-most rule) |
| `WHERE age = 30 AND city='Cairo'`   | ✔️ Yes (optimizer reorders)  |

Rule:
>Use the leading column(s) of the index for it to work.

### 📌 The 80/20 Rule
> 20% of queries cause 80% of performance problems.

Fix those queries first using:
- indexes 
- avoiding full scans 
- caching repeated queries 
- rewrite slow patterns

## ⚙️ QUERY OPTIMIZATION
### 📌 EXPLAIN PLAN / EXPLAIN ANALYZE
Tools to see how DB plans to execute a query.

EXPLAIN ANALYZE
```postgresql
SELECT * FROM orders WHERE customer_id = 55;
```
It reveals:
- full table scans vs index usage 
- cost estimates 
- join methods & sort methods 
- disk vs memory usage

### 📌 Cost-Based Optimization
DB picks the lowest cost execution plan using:
- statistics 
- selectivity (how many rows match)
- I/O cost 
- CPU cost

### 📌 Query Execution Steps
1. Parse SQL 
2. Convert to Relational Algebra 
3. Apply Optimizations 
4. Generate Query Plan 
5. Execute

### 🔁 Algorithms In Query Processing
#### Algorithms for SELECT
| Type          | Best When                     |
| ------------- | ----------------------------- |
| Linear scan   | No index                      |
| Binary search | Ordered / indexed             |
| Index scan    | Index exists on filter column |

#### Algorithms for JOIN
| Method           | Works Best When                      |
| ---------------- | ------------------------------------ |
| Nested Loop Join | Small tables / indexed joins         |
| Hash Join        | Equality joins (`=`) on large tables |
| Merge Join       | Sorted inputs, range operations      |

#### External Sorting
Used when data is too large to fit memory:
- External Merge Sort 
- Replacement Selection

#### Algorithms for PROJECT
- Remove unwanted columns early to reduce data movement

#### SET Operations
- Use hashing / sorting to remove duplicates and match records

#### Aggregation / Outer Joins
- Temp hash tables for grouping 
- Sorting + merge for outer joins

### ❌ Avoid Slow Patterns
| Problem                                       | Instead Use                                          |
| --------------------------------------------- | ---------------------------------------------------- |
| `SELECT *`                                    | Specify needed columns                               |
| Functions in WHERE: `WHERE YEAR(date) = 2024` | `WHERE date >= '2024-01-01' AND date < '2025-01-01'` |
| `OR` with many values                         | `IN ( ... )`                                         |
| Leading wildcard: `LIKE '%name'`              | Avoid; cannot use index                              |
| Implicit type conversion                      | Ensure types match                                   |

## 📌 Optimization Strategies
### 🔹 Heuristic Optimization (Rule-Based)
- Apply algebraic transformations 
- Push down selections early 
- Remove duplicates early 
- Reorder operations to reduce data

### 🔹 Using Selectivity
> Selectivity = fraction of rows that match the condition  
Higher selectivity = condition filters more = better

Example:
```postgresql
WHERE email = 'x@z.com' -- highly selective
WHERE gender = 'M'      -- low selectivity
```
### 🔹 Semantic Query Optimization
Rewrite queries using logical rules to simplify.

Example:
```postgresql
SELECT *
FROM employees
WHERE age > 18 AND age > 21; -- redundant
```
Simplified to:
```postgresql
WHERE age > 21;
```
### 🔹 Pipelining (Non-Materialized Ops)
SQL engine streams results of operations directly to the next step rather than saving intermediate tables.
- Faster 
- Saves memory 
- Real-time processing