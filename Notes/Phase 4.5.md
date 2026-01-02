# 🚀 Phase 4.5 — Data Types & Storage Formats

## 1️⃣ Numeric Data Types & Physical Representation
Databases store numbers differently based on size, precision, and behavior.

| Type                  | Stores                    | Examples                    | Notes                       |
| --------------------- | ------------------------- | --------------------------- | --------------------------- |
| **INT / INTEGER**     | Whole numbers             | 10, 25, -100                | Fixed 4 bytes               |
| **SMALLINT / BIGINT** | Smaller/larger integers   | -32768 → 32767, etc.        | Use based on range needs    |
| **FLOAT / REAL**      | Approximate decimals      | 3.14159                     | Not exact — rounding errors |
| **DOUBLE**            | Higher precision float    | 3.1415926535                | For scientific values       |
| **DECIMAL(p,s)**      | Exact fixed-point numbers | DECIMAL(10,2) → 99999999.99 | Exact storage — no rounding |

📌 Rule of Thumb
- Money? → DECIMAL († Never store money in FLOAT)
- Scientific / sensor data? → FLOAT / DOUBLE 
- Counts / IDs → INT / BIGINT

💡 Rounding issues:
```postgresql
0.1 + 0.2 == 0.30000000000000004   -- FLOAT problem
```

## 2️⃣ Strings: CHAR vs VARCHAR vs TEXT
| Type           | When to Use          | Storage Behavior                      | Example                   |
| -------------- | -------------------- | ------------------------------------- | ------------------------- |
| **CHAR(n)**    | Fixed length data    | Always reserves full length           | Country codes: "EG" "USA" |
| **VARCHAR(n)** | Variable length text | Stores only actual + 1 byte metadata  | Names, emails             |
| **TEXT**       | Long text            | Stored separately from main table row | Articles, descriptions    |

Performance Insight
- `CHAR` is faster for uniform size data. 
- `VARCHAR` is best for 95% of cases. 
- `TEXT` can’t always be indexed easily.

## 📌 Example
```postgresql
CHAR(2) → 'EG'
VARCHAR(50) → 'Ahmed Mohamed Habib'
TEXT → long biography...
```
## 3️⃣ Binary Data – BLOB
BLOB = Binary Large Object
- Stores raw binary data 
- Images, PDFs, audio, hashes

⚠️ Performance caution   
Storing large files IN the DB can slow backups & replication.

Better Strategy:
- Store file in cloud (S3, Google Cloud, file server)
- Store only the file path/URL in the DB

## 4️⃣ JSON / XML Columns

### JSON
- Supported natively in PostgreSQL, MySQL, MongoDB
- Great for semi-structured or flexible schema data

Example:
```postgresql
{
"name": "Ahmed",
"preferences": {"theme": "dark", "language": "ar"}
}
```
🎯 Use cases:
- optional attributes 
- configuration data 
- product metadata (sizes, colors vary)

⚠️ Don’t overuse. If structure is known → use normal columns.

### XML
- Same idea but markup-based 
- Mostly legacy systems or enterprise systems (Oracle, SQL Server)

📌 JSON is the modern standard, XML is mostly for compatibility.

## 5️⃣ Collation & Encoding
### Encoding

How characters are stored as bytes.

| Encoding   | Notes                                                   | When to Use                              |
| ---------- | ------------------------------------------------------- | ---------------------------------------- |
| **UTF-8**  | Variable length; efficient for English + Arabic + emoji | Best default choice                      |
| **UTF-16** | Fixed-ish length; bigger for Latin text                 | Heavy; used sometimes in Windows systems |

👍 Always choose UTF-8 unless forced otherwise.

### Collation
Rules for:
- sorting 
- comparison 
- case sensitivity

Examples:
- `utf8_general_ci` → case-insensitive 
- `utf8_bin` → case-sensitive binary comparison

💡 If you need case-sensitive passwords or tags, use `_bin`.

## 6️⃣ Time Zones & Timestamp Caveats
Time-related columns are dangerous if misunderstood.

| Type        | Stores                    | Notes                        |
| ----------- | ------------------------- | ---------------------------- |
| `DATE`      | YYYY-MM-DD                | No time                      |
| `TIME`      | HH:MM:SS                  | No date                      |
| `DATETIME`  | Date + Time (no timezone) | Good for local systems       |
| `TIMESTAMP` | Date + Time in UTC        | Converts to session timezone |

📌 Example Problem:
If someone in Egypt inserts `2025-01-01 22:00` into `TIMESTAMP`, a user in New York may see `2025-01-01 15:00`.

Best Practice:
- Store in UTC 
- Convert to local timezone in the application layer
