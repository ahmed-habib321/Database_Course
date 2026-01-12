# 🚀 Phase 13 — NoSQL & Modern Data Systems
When SQL is not enough

Phase 13 is where we broaden our toolkit beyond traditional relational databases.  
Modern applications often require flexible schemas, massive scalability, or specialized query patterns that SQL alone can’t handle efficiently.


NoSQL databases are designed to handle highly scalable, distributed, or schema-flexible workloads.

## 1️⃣ Key-Value Stores
- System Example: Redis, DynamoDB 
- Concept: Data stored as `key → value` pairs 
- Pros: Ultra-fast lookups, simple, in-memory options 
- Use Cases:
  - Caching 
  - Sessions 
  - Leaderboards 
  - Real-time analytics

Example (Redis):
```postgresql
SET user:1001 "Ahmed"
GET user:1001
```
## 2️⃣ Document Stores
- System Example: MongoDB, CouchDB 
- Concept: Data stored as documents (JSON/BSON), flexible schema 
- Pros: Easy to evolve schema, nested structures 
- Use Cases:
  - Content management 
  - Event logging 
  - E-commerce product catalogs

Example (MongoDB):
```postgresql
{
"name": "Ahmed",
"age": 24,
"courses": ["DB", "AI"]
}
```
Query:
```postgresql
db.students.find({ age: { $gte: 20 } })
```

## 3️⃣ Columnar / Wide-Column Stores
- System Example: Cassandra, HBase 
- Concept: Data stored by columns instead of rows → great for aggregations 
- Pros: High write scalability, distributed architecture 
- Use Cases:
  - IoT sensor data 
  - Time-series 
  - Real-time analytics

Example:

| user_id | timestamp | metric1 | metric2 |
| ------- | --------- | ------- | ------- |

Query only reads specific columns → very fast.

## 4️⃣ Graph Databases
- System Example: Neo4j, Amazon Neptune 
- Concept: Stores nodes (entities) and edges (relationships)
- Pros: Optimized for relationship-heavy data 
- Use Cases:
  - Social networks 
  - Recommendations 
  - Fraud detection 
  - Knowledge graphs

Example (Cypher query in Neo4j):
```postgresql
MATCH (a:User)-[:FRIEND]->(b:User)
WHERE a.name = 'Ahmed'
RETURN b.name
```

### 5️⃣ Search / Full-Text Systems
- System Example: Elasticsearch, Solr 
- Concept: Index-based search optimized for text queries 
- Pros: Fast full-text search, analytics, ranking 
- Use Cases:
  - Search engines 
  - Logging & monitoring 
  - Autocomplete / suggestions

Example (Elasticsearch query DSL):
```postgresql
{
"query": {
"match": { "title": "database" }
}
}
```
## 6️⃣ Choosing the Right Modern Data System
| Factor                           | Recommendation       |
| -------------------------------- | -------------------- |
| Ultra-fast key lookup            | Redis / DynamoDB     |
| Flexible schema / nested objects | MongoDB / CouchDB    |
| High write throughput, analytics | Cassandra / HBase    |
| Relationships & graph traversal  | Neo4j / Graph DB     |
| Full-text search                 | Elasticsearch / Solr |

## 7️⃣ Hybrid Approaches
- Many modern systems combine SQL + NoSQL 
- Example: Use PostgreSQL for transactions + Redis for caching + Elasticsearch for search 
- This approach leverages strengths of multiple systems.

At this point, if you has completed Phase 1–13, 
you now have a full modern database mastery roadmap:
- SQL & relational theory 
- Transactions, indexing, performance tuning 
- Storage engines & architecture 
- Security, backup, replication 
- NoSQL & distributed/modern systems