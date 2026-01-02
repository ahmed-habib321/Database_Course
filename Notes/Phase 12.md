# 🚀 Phase 12 — Database Security
Protection, access control, SQL injection defense

Phase 12 is where we shift from building databases to protecting them. Security isn’t just “adding passwords”—it’s about access control, protecting sensitive data, and preventing attacks.


## 1️⃣ Fundamental Principles
### a) Rotate Database Credentials
- Change passwords periodically 
- Avoid hardcoding credentials in applications 
- Use secrets managers (e.g., HashiCorp Vault, AWS Secrets Manager)

### b) No Root Connections from Applications
- Never let apps connect as admin/root 
- Use a dedicated low-privilege account for each service

### c) Privilege Management (Least Privilege)
- Give users only the permissions they need 
- Example:
```postgresql
GRANT SELECT, INSERT ON orders TO app_user;
```
### d) Statistical Database Security
- Prevent inference attacks using statistical queries 
- Limit what aggregates users can access to avoid exposing sensitive info indirectly

### e) Flow Control
- Monitor data access patterns to prevent exfiltration 
- Detect abnormal activity (large downloads, repeated queries, etc.)

## 2️⃣ SQL Injection & Prepared Statements
SQL Injection = when user input manipulates SQL queries.

❌ Vulnerable example:
```postgresql
"SELECT * FROM users WHERE username = '" + user_input + "'";
```
✅ Safe version: Prepared Statements / Parameterized Queries
```postgresql
-- Example in MySQL
PREPARE stmt FROM 'SELECT * FROM users WHERE username = ?';
SET @user = 'Ahmed';
EXECUTE stmt USING @user;
```
Key points:
- Never concatenate user input into SQL directly 
- Always validate and sanitize input

## 3️⃣ GRANT / REVOKE — Access Control
Control who can do what on each object.
```postgresql
-- Grant privileges
GRANT SELECT, INSERT ON orders TO app_user;

-- Revoke privileges
REVOKE INSERT ON orders FROM app_user;
```
- Works for users, roles, groups 
- Helps implement least privilege principle

## 4️⃣ Role-Based Access Control (RBAC)
- Create roles with specific privileges 
- Assign roles to users instead of individual permissions
```postgresql
CREATE ROLE sales;
GRANT SELECT, INSERT ON orders TO sales;
GRANT sales TO user_john;
```
- Easier to manage multiple users 
- Reduces mistakes and privilege creep

## 5️⃣ Encryption Basics
- At rest → encrypt database files on disk (Transparent Data Encryption - TDE)
- In transit → use SSL/TLS for connections 
- Column-level encryption → encrypt sensitive fields (SSN, passwords)

Passwords must never be stored in plain text — use hashing with salt:

## 6️⃣ Sanitization & Validation
- Sanitization: remove dangerous characters / patterns 
- Validation: ensure input is the correct type/format

✅ Example:
- Ensure `age` is numeric 
- Ensure email follows a valid format 
- Strip SQL meta-characters for non-prepared statements