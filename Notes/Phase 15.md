# 🚀 Phase 15 — Data Warehousing & OLAP
Analytics, reporting & business intelligence 

Phase 15 is where databases meet analytics and business intelligence. Unlike OLTP systems (transactional databases), this phase focuses on aggregating, summarizing, and analyzing large amounts of historical data for decision-making.

## 1️⃣ Characteristics of Data Warehouses
- Subject-oriented → Organized by business areas (sales, customers)
- Integrated → Data from multiple sources cleaned and standardized 
- Time-variant → Historical data stored, not just current snapshot 
- Non-volatile → Read-heavy; updates are done via ETL, not frequent transactions

## 2️⃣ Building a Data Warehouse
Steps:
1. Identify business requirements (what analytics/reporting is needed)
2. Identify data sources (operational databases, logs, external systems)
3. ETL/ELT pipeline design (Extract, Transform, Load / Extract, Load, Transform)
4. Schema design (star, snowflake, fact/dimension tables)
5. Aggregation, indexing, and materialized views for performance

## 3️⃣ Typical Functionality
- Query historical data 
- Generate reports, dashboards, KPIs 
- Support multidimensional analysis (OLAP)
- Perform trend analysis, forecasting, segmentation 

## 4️⃣ Problems & Open Issues
- Data quality & consistency across sources 
- Scalability for massive datasets 
- ETL/ELT performance 
- Keeping warehouse synchronized with source systems 
- Schema evolution over time

## 5️⃣ Star Schema & Snowflake Schema
### Star Schema
- Central Fact Table surrounded by Dimension Tables 
- Simple, denormalized → fast for queries

Example:
```postgresql
        Customer
            |
Product - Sales - Time
            |
          Store
```
### Snowflake Schema
- Dimension tables are normalized into multiple related tables 
- Reduces redundancy but increases joins → slightly slower queries

Example:
Customer → Customer_Address → Customer_Region

## 6️⃣ Fact vs Dimension Tables
| Type                | Purpose                      | Example Columns                               |
| ------------------- | ---------------------------- | --------------------------------------------- |
| **Fact Table**      | Stores measurements, metrics | Sales Amount, Quantity, Profit                |
| **Dimension Table** | Stores descriptive context   | Customer Name, Product Category, Time, Region |

## 7️⃣ OLAP Cubes
Multidimensional data structures for analytical queries.
- Rollup: Aggregate data to higher levels (day → month → year)
- Drill-down: Go from summarized data to detailed levels 
- Slice: Select a single dimension value (e.g., all sales in Egypt)
- Dice: Select multiple dimension values (e.g., sales in Egypt + 2024)

## 8️⃣ ETL / ELT Pipelines
ETL: Extract → Transform → Load (classic)  
ELT: Extract → Load → Transform (modern, big data systems)
- Ensures data cleaning, deduplication, and consistency 
- Loads data into warehouse ready for analysis

## 9️⃣ Materialized Views
- Precomputed queries stored as tables for faster analytics 
- Used for summary reports or aggregated cubes
```postgresql
CREATE MATERIALIZED VIEW monthly_sales AS
SELECT customer_id, SUM(amount) AS total
FROM sales
GROUP BY customer_id, MONTH(order_date);
```
- Speeds up OLAP queries, reduces computational overhead

## 🎯 Phase 15 Key Takeaways
- Data warehouses are read-optimized, historical, integrated 
- ETL/ELT pipelines are essential for data cleaning & loading 
- Star vs Snowflake → tradeoff between simplicity vs normalization 
- Fact tables store metrics, dimension tables store context 
- OLAP operations (rollup, drill-down, slice, dice) enable multidimensional analysis 
- Materialized views & cubes → speed up analytics queries