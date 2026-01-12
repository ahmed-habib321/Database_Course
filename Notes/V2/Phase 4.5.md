# Database Data Types & Storage Formats

## Understanding Numeric Data Types

Databases don't just store numbers as "numbers" - they use different formats based on what kind of number you're storing and how precise it needs to be.

### Integer Types

Integers are whole numbers without decimal points. Databases offer different sizes to balance storage space with the range of values you need:

**INT (INTEGER)** is your standard whole number type. It uses 4 bytes of storage and can hold values from about -2 billion to +2 billion. This is perfect for most everyday uses like user IDs, quantities, or ages.

**SMALLINT** uses only 2 bytes and stores numbers from -32,768 to 32,767. If you're storing something like "number of items in stock" for a small shop, this saves space.

**BIGINT** is the heavyweight, using 8 bytes to store massive numbers (up to 9 quintillion). Use this for things that could grow very large, like financial transactions in a high-volume system or unique identifiers in a massive dataset.

### Decimal Types

Here's where things get interesting. Not all decimal numbers are stored the same way:

**FLOAT and DOUBLE** store approximate decimal values. They're fast and efficient but have a critical flaw: they can't represent all decimal numbers exactly. This happens because computers store these numbers in binary (base 2), and some decimal fractions can't be perfectly represented in binary. For example, 0.1 + 0.2 might give you 0.30000000000000004 instead of 0.3. This is fine for scientific measurements or sensor data where tiny imprecisions don't matter, but catastrophic for money.

**DECIMAL(p,s)** solves the precision problem. The "p" is total digits, and "s" is digits after the decimal point. DECIMAL(10,2) can store up to 99,999,999.99 - perfect for currency. It stores numbers exactly as you enter them, with no rounding errors. The tradeoff is slightly slower performance and more storage space.

**Golden Rule**: Always use DECIMAL for money. Always. No exceptions. Financial calculations with FLOAT will eventually create errors that compound over time.

## Text Storage: Choosing the Right String Type

### CHAR - Fixed Length

CHAR(n) reserves exactly n characters of space, even if you don't use them all. If you store "EG" in a CHAR(5) column, it pads it with spaces to make it 5 characters. This seems wasteful, but it's actually faster for the database to process because every value is the same size. Use CHAR for data that's always the same length: country codes, US state abbreviations, yes/no flags stored as "Y" or "N".

### VARCHAR - Variable Length

VARCHAR(n) only uses the space it needs, plus one or two bytes for metadata that tracks the actual length. Storing "Ahmed" in VARCHAR(50) only uses 6 bytes total instead of 50. This is your go-to choice for most text: names, email addresses, product titles, anything that varies in length. The maximum n you set is just an upper limit for validation.

### TEXT

TEXT is for long-form content: blog posts, product descriptions, user comments. Databases often store TEXT data separately from the main table data (in a separate storage area called "TOAST" in PostgreSQL). This keeps your main table rows small and fast to scan. The downside is that TEXT columns can be trickier to index for searching, and some databases limit how you can use them in queries.

**Performance tip**: VARCHAR(255) is often faster than TEXT for moderately long content because it stays in the main table structure.

## Binary Data and BLOBs

BLOB stands for Binary Large Object. It stores raw binary data: images, PDFs, audio files, compressed archives, encrypted data, anything that isn't text.

Here's the critical insight: just because you *can* store files in your database doesn't mean you *should*. Large BLOBs make your database backup files huge, slow down replication between servers, and make the database do work it's not optimized for.

**Better approach**: Store files in object storage (Amazon S3, Google Cloud Storage, Azure Blob Storage) or a file server. In your database, just store the URL or file path as a VARCHAR. This keeps your database lean and puts file storage where it belongs.

**When to use BLOBs**: Small binary data like password hashes, cryptographic keys, or thumbnails under 100KB where you need atomic transactions with other data.

## Semi-Structured Data: JSON and XML

### JSON Columns

Modern databases (PostgreSQL, MySQL 5.7+, SQL Server) support native JSON storage. This is powerful for data that doesn't fit neatly into fixed columns.

Imagine an e-commerce product table. T-shirts have sizes and colors, but laptops have processor specs and RAM. Instead of creating 50 different columns for every possible attribute, you can use a JSON column:

```json
{
  "color": "blue",
  "sizes": ["S", "M", "L"],
  "material": "cotton"
}
```

JSON is also perfect for user preferences, feature flags, or any "optional attributes" scenario where different records have different fields.

**Warning**: Don't abuse JSON. If every single record has the same structure, use regular columns. JSON is for flexibility, not as a replacement for proper database design. You also can't enforce data integrity as strictly, and queries on JSON fields are usually slower than on normal columns.

### XML

XML does the same thing as JSON but with markup tags. It's verbose and harder to work with, but you'll see it in older enterprise systems (especially Oracle) and when integrating with legacy software. Unless you're forced to use it for compatibility, choose JSON.

## Character Encoding and Collation

### Encoding: How Characters Become Bytes

Encoding defines how text characters are converted to binary data for storage.

**UTF-8** is your default choice. It uses 1 byte for English letters, 2-3 bytes for Arabic or Chinese characters, and 4 bytes for emoji. It's efficient, universal, and handles every language and symbol you'll need. Unless you have a very specific reason, always use UTF-8.

**UTF-16** uses 2 bytes minimum per character. It's common in Windows systems and Java internals but wastes space for English text. Avoid it in databases unless you're forced to match an existing system.

**Latin1** and other single-byte encodings are legacy options. They're slightly faster but can't handle non-Western text. Don't use them for new projects.

### Collation: How Text Is Compared and Sorted

Collation defines the rules for comparing and sorting text. This affects ORDER BY queries, string comparisons, and uniqueness checks.

**utf8_general_ci** (ci = case insensitive) treats "Ahmed" and "ahmed" as the same. Good for usernames or email addresses where case shouldn't matter.

**utf8_bin** does binary comparison - it's case sensitive and faster. Use this when case matters: passwords, API keys, hashtags.

**Language-specific collations** like utf8_arabic_ci understand language-specific sorting rules. Arabic collations know how to sort diacritics correctly.

**Real-world impact**: If you use case-insensitive collation on a username column, users can't create both "Ahmed" and "ahmed" as separate accounts - the database sees them as duplicates.

## Date and Time Types: The Timezone Trap

Time-related data is one of the most misunderstood aspects of databases. Get it wrong and you'll have bugs that are nearly impossible to debug.

### Basic Types

**DATE** stores just the calendar date: 2025-01-12. No time component at all.

**TIME** stores just the clock time: 14:30:00. No date component.

**DATETIME** stores both date and time together: 2025-01-12 14:30:00. Critical detail: it doesn't store any timezone information. It's just the numbers you see.

**TIMESTAMP** stores a point in time as a UTC value. Here's where it gets tricky.

### The Timezone Problem

Imagine you're in Egypt (UTC+2) and you insert a meeting time: "2025-01-12 16:00". If you use DATETIME, that's what gets stored - just those numbers. When someone in New York (UTC-5) queries it, they see 16:00, which is wrong. Your 4pm is their 9am.

If you use TIMESTAMP, the database converts your local time to UTC before storing. It might store "2025-01-12 14:00" (16:00 minus 2 hours). When the New York user queries it, their database session (set to their timezone) automatically converts it back: they see 09:00, which is correct.

### Best Practice

**Store everything in UTC using TIMESTAMP columns.** This gives you a single, unambiguous reference point. When you retrieve data, convert it to the user's local timezone in your application code, not in the database. This keeps your data layer clean and your application layer flexible.

**Exception**: If you truly have local-only data that never needs timezone conversion (like "store opening hours"), DATETIME is simpler.

### Common Mistake

Never store times as strings (VARCHAR). You'll lose the ability to do time math, compare times properly, or use database time functions. If you inherit a database that does this, plan a migration ASAP.