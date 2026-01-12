# Database Security - Complete Guide

Security isn't an afterthought—it's the foundation that keeps your data safe from unauthorized access, manipulation, and theft. This guide covers the essential principles and practices for securing databases in production environments.

---

## Core Security Principles

### Credential Rotation and Management

Your database credentials are keys to the kingdom. Treat them accordingly:

**Why rotate credentials?** Even if no breach occurs, regular rotation limits the damage from undetected compromises. If credentials are leaked or logged somewhere you didn't intend, changing them periodically reduces the window of vulnerability.

**Never hardcode credentials.** Embedding passwords directly in your application code means anyone with access to the codebase has database access. It also makes rotation nearly impossible without redeploying code.

**Use secrets managers.** Tools like HashiCorp Vault, AWS Secrets Manager, or Azure Key Vault provide:
- Centralized credential storage
- Automatic rotation capabilities
- Audit logs of who accessed what credentials
- Fine-grained access policies

**Example workflow:**
1. Application requests database credentials from secrets manager at startup
2. Secrets manager returns current credentials
3. Application uses credentials to connect
4. Credentials rotate weekly without code changes

### The Principle of Least Privilege

Give each user and application only the minimum permissions needed to do their job—nothing more.

**Why this matters:** If an application gets compromised, the attacker inherits whatever permissions that application has. An app that can only SELECT and INSERT on a single table is far less dangerous when compromised than one with DROP TABLE privileges.

**Example scenario:**
- Your web application needs to display products and record orders
- It should NOT be able to delete users, modify schemas, or access payroll data
- Create a dedicated database user with only these permissions:

```sql
CREATE USER web_app_user WITH PASSWORD 'secure_password';
GRANT SELECT ON products TO web_app_user;
GRANT SELECT, INSERT ON orders TO web_app_user;
GRANT SELECT, INSERT ON order_items TO web_app_user;
```

### No Root/Admin Connections from Applications

Your database administrator account (root, postgres, sa) has unlimited power. Applications should never connect using these accounts.

**Why?** 
- Root accounts can drop databases, modify schemas, and access all data
- If your application is compromised, attackers gain full database control
- You can't audit what normal operations look like versus attacks

**Instead:**
- Use root only for administrative tasks (schema changes, user management)
- Create service-specific accounts with limited privileges
- Each microservice gets its own database user

### Statistical Database Security

This addresses a subtle but serious threat: inference attacks.

**The problem:** Even without direct access to sensitive data, attackers can infer individual records through statistical queries.

**Example attack:**
```sql
-- Attacker knows there are 100 employees
SELECT AVG(salary) FROM employees;  -- Returns $75,000

-- Attacker adds WHERE clause
SELECT AVG(salary) FROM employees WHERE name != 'John Smith';  -- Returns $74,500

-- Simple math reveals John Smith's salary: 
-- 100 * $75,000 - 99 * $74,500 = $125,500
```

**Defenses:**
- Require minimum group sizes for aggregate queries (e.g., at least 10 records)
- Add statistical noise to results
- Track query patterns to detect reconnaissance
- Restrict access to aggregate functions on sensitive columns

### Flow Control and Monitoring

Watch *how* users access data, not just *what* they access.

**Red flags:**
- A user who normally views 50 records suddenly downloads 50,000
- Repeated queries against sensitive tables outside normal hours
- Sequential queries that enumerate all records in a table
- Unusual query patterns (e.g., a customer service rep querying executive salaries)

**Implementation:**
- Enable database audit logging
- Set up alerts for unusual access patterns
- Monitor data export volumes
- Track which users access which tables

---

## SQL Injection - The Most Common Attack

SQL injection occurs when user input is inserted directly into SQL queries, allowing attackers to manipulate the query's logic.

### How It Works

**Vulnerable code:**
```python
username = request.form['username']
password = request.form['password']
query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'"
```

**Attack:**
User enters: `admin' OR '1'='1` as username

**Resulting query:**
```sql
SELECT * FROM users WHERE username = 'admin' OR '1'='1' AND password = '...'
```

Since `'1'='1'` is always true, this returns all users and often bypasses authentication.

**More dangerous attack:**
User enters: `'; DROP TABLE users; --` as username

**Resulting query:**
```sql
SELECT * FROM users WHERE username = ''; DROP TABLE users; --' AND password = '...'
```

This executes multiple statements: the SELECT, then drops your entire users table. The `--` comments out the rest.

### The Solution: Prepared Statements

Prepared statements (also called parameterized queries) separate SQL code from data. The database treats user input as pure data, never as SQL commands.

**PostgreSQL example:**
```sql
PREPARE get_user (TEXT) AS
  SELECT * FROM users WHERE username = $1;

EXECUTE get_user('admin'' OR ''1''=''1');
-- This searches for a user literally named "admin' OR '1'='1"
-- It doesn't execute the OR condition as SQL
```

**Python example using parameters:**
```python
cursor.execute(
    "SELECT * FROM users WHERE username = %s AND password = %s",
    (username, password)
)
```

**Why this works:**
- The SQL structure is defined first
- User input fills in placeholders
- The database never interprets user input as SQL syntax
- Even if user enters SQL code, it's treated as a search string

**Additional defenses:**
- Input validation: Ensure data matches expected format (email looks like email, age is numeric)
- Input sanitization: Remove or escape special characters
- Web Application Firewall (WAF): Can detect and block SQL injection attempts
- Never display raw SQL errors to users (they reveal schema information)

---

## Access Control with GRANT and REVOKE

SQL databases have built-in permission systems. Use them.

### Basic Syntax

**Grant permissions:**
```sql
GRANT privilege_type ON object TO user;
```

**Revoke permissions:**
```sql
REVOKE privilege_type ON object FROM user;
```

### Common Privilege Types

- `SELECT` - Read data
- `INSERT` - Add new rows
- `UPDATE` - Modify existing rows
- `DELETE` - Remove rows
- `TRUNCATE` - Empty tables
- `REFERENCES` - Create foreign keys
- `TRIGGER` - Create triggers
- `ALL PRIVILEGES` - Everything (use sparingly)

### Practical Examples

**Read-only analytics user:**
```sql
GRANT SELECT ON ALL TABLES IN SCHEMA public TO analyst_user;
```

**Application user with specific access:**
```sql
GRANT SELECT, INSERT, UPDATE ON customers TO app_user;
GRANT SELECT, INSERT ON orders TO app_user;
-- Note: No DELETE or DROP permissions
```

**Temporary elevated access:**
```sql
GRANT UPDATE ON products TO marketing_user;
-- After marketing campaign update
REVOKE UPDATE ON products FROM marketing_user;
```

**Column-level permissions:**
```sql
-- Marketing can see customer names/emails but not payment info
GRANT SELECT (customer_id, name, email) ON customers TO marketing_user;
```

---

## Role-Based Access Control (RBAC)

Instead of granting permissions directly to each user, create roles that represent job functions.

### Why Use Roles?

**Without roles:**
- New employee joins → admin grants 15 individual permissions
- Employee changes departments → admin revokes 15 permissions, grants 12 new ones
- Easy to make mistakes and create security gaps

**With roles:**
- New employee joins → admin assigns them to the "sales" role
- Employee changes departments → admin changes role from "sales" to "support"
- All permissions change automatically

### Creating and Using Roles

**Define roles:**
```sql
-- Create roles for different job functions
CREATE ROLE sales;
CREATE ROLE support;
CREATE ROLE analyst;

-- Grant permissions to roles
GRANT SELECT, INSERT ON customers TO sales;
GRANT SELECT, INSERT, UPDATE ON orders TO sales;
GRANT SELECT ON products TO sales;

GRANT SELECT ON customers TO support;
GRANT SELECT, UPDATE ON orders TO support;

GRANT SELECT ON ALL TABLES IN SCHEMA public TO analyst;
```

**Assign users to roles:**
```sql
-- New sales team member
CREATE USER john_doe WITH PASSWORD 'secure_password';
GRANT sales TO john_doe;

-- Support team member
CREATE USER jane_smith WITH PASSWORD 'secure_password';
GRANT support TO jane_smith;
```

**Hierarchical roles:**
```sql
-- Create a manager role with all sales permissions plus more
CREATE ROLE sales_manager;
GRANT sales TO sales_manager;  -- Inherit sales permissions
GRANT DELETE ON orders TO sales_manager;  -- Additional permission
GRANT UPDATE ON products TO sales_manager;

-- Assign manager
GRANT sales_manager TO susan_lee;
```

### Best Practices

- Name roles after job functions, not people
- Document what each role is for
- Regularly audit role memberships
- Use role hierarchies to simplify management
- Test roles with test accounts before deploying

---

## Encryption

Encryption protects data even if physical security fails.

### Encryption at Rest

Protects database files stored on disk. If someone steals the hard drive, data is unreadable without the encryption key.

**Methods:**
- **Transparent Data Encryption (TDE):** Database automatically encrypts/decrypts data. Applications don't change.
- **Filesystem-level encryption:** Encrypt the entire disk partition
- **Column-level encryption:** Encrypt specific sensitive columns

**PostgreSQL example:**
```sql
-- Using pgcrypto extension for column encryption
CREATE EXTENSION pgcrypto;

-- Encrypt social security numbers
INSERT INTO employees (name, ssn_encrypted)
VALUES ('John Doe', pgp_sym_encrypt('123-45-6789', 'encryption_key'));

-- Decrypt when needed
SELECT name, pgp_sym_decrypt(ssn_encrypted, 'encryption_key') AS ssn
FROM employees;
```

### Encryption in Transit

Protects data traveling between application and database. Prevents network eavesdropping.

**Implementation:**
- Use SSL/TLS for all database connections
- Require encrypted connections (reject unencrypted attempts)
- Use strong cipher suites
- Keep SSL certificates up to date

**PostgreSQL SSL connection:**
```sql
-- Require SSL in postgresql.conf
ssl = on
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'

-- Force SSL for specific users
ALTER USER app_user SET ssl = on;
```

### Password Storage

**Never store passwords in plain text.** Use hashing with salt.

**Wrong:**
```sql
INSERT INTO users (username, password) VALUES ('john', 'MyPassword123');
```

**Right:**
```python
import bcrypt

# Hashing a password (during registration)
password = 'MyPassword123'
hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())

# Store hashed password
cursor.execute("INSERT INTO users (username, password_hash) VALUES (%s, %s)",
               ('john', hashed))

# Verifying password (during login)
stored_hash = get_password_hash_from_db('john')
if bcrypt.checkpw(password.encode('utf-8'), stored_hash):
    print("Login successful")
```

**Why hashing is one-way:**
- You can't decrypt a hash to get the original password
- To verify, hash the entered password and compare hashes
- Even if database is stolen, passwords remain protected

**Salt prevents rainbow table attacks:**
- Salt is random data added to password before hashing
- Same password produces different hashes for different users
- Attackers can't use precomputed hash tables

---

## Input Validation and Sanitization

Defense in depth: Don't rely solely on prepared statements.

### Validation

Ensure input matches expected format before processing.

**Examples:**
```python
import re

# Email validation
def is_valid_email(email):
    pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
    return re.match(pattern, email) is not None

# Age validation
def is_valid_age(age):
    try:
        age_int = int(age)
        return 0 < age_int < 150
    except ValueError:
        return False

# Username validation (alphanumeric only)
def is_valid_username(username):
    return username.isalnum() and 3 <= len(username) <= 20
```

### Sanitization

Remove or escape dangerous characters.

**When to sanitize:**
- Legacy systems that can't use prepared statements
- Data displayed back to users (prevent XSS)
- File paths and system commands

**Examples:**
```python
# Escape SQL special characters (but prepared statements are better)
def escape_sql(value):
    return value.replace("'", "''").replace(";", "")

# Remove HTML tags
import html
def sanitize_html(text):
    return html.escape(text)

# Whitelist allowed characters
def sanitize_filename(filename):
    return re.sub(r'[^a-zA-Z0-9._-]', '', filename)
```

### Whitelist vs Blacklist

**Whitelist (recommended):** Only allow known-good characters
```python
# Only allow letters, numbers, and specific punctuation
if re.match(r'^[a-zA-Z0-9\s.,!?-]+$', user_input):
    process_input(user_input)
```

**Blacklist (weaker):** Try to block known-bad characters
```python
# Attempt to block SQL injection characters (incomplete, not recommended)
if any(char in user_input for char in ["'", '"', ';', '--']):
    reject_input()
```

**Why whitelist is better:** Attackers constantly find new ways to bypass blacklists. Whitelist only allows what you explicitly permit.

---

## Security Checklist

**Before deploying a database:**
- [ ] Use prepared statements for all queries
- [ ] Create service-specific database users (no root connections)
- [ ] Grant minimum necessary privileges
- [ ] Enable encryption at rest (TDE)
- [ ] Require SSL/TLS for connections
- [ ] Hash passwords with bcrypt or similar
- [ ] Implement role-based access control
- [ ] Set up audit logging
- [ ] Configure automated backups (encrypted)
- [ ] Use secrets manager for credentials
- [ ] Validate and sanitize all inputs
- [ ] Monitor for unusual access patterns
- [ ] Keep database software updated
- [ ] Restrict network access (firewall rules)
- [ ] Document security procedures

**Regular maintenance:**
- [ ] Rotate credentials quarterly
- [ ] Review user permissions monthly
- [ ] Audit access logs weekly
- [ ] Test backup restoration quarterly
- [ ] Update security patches immediately
- [ ] Review and test incident response plan

---

## Real-World Scenario

**Company:** E-commerce site with customer data

**Security implementation:**

1. **Credentials:** Stored in AWS Secrets Manager, rotated every 90 days
2. **Application user:** Can only SELECT products, INSERT/UPDATE orders
3. **Admin user:** Used only for schema migrations, never from application
4. **Roles created:**
   - `customer_service` - Read customers, read/update orders
   - `warehouse` - Read orders, update inventory
   - `analyst` - Read-only access to anonymized data
5. **Encryption:**
   - TDE enabled on database
   - Credit card data encrypted at column level
   - SSL required for all connections
   - Passwords hashed with bcrypt
6. **Monitoring:**
   - Alert if any user downloads more than 1000 records
   - Alert on queries to credit card columns outside business hours
   - Daily audit log review

**Result:** When application was compromised by an injection vulnerability in a different part of the system, the attacker could only access the limited data available to the application user. No passwords were exposed (hashed), no credit cards were stolen (encrypted and limited access), and unusual query patterns triggered alerts that led to quick discovery and remediation.