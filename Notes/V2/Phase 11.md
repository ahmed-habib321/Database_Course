# Stored Procedures, Triggers & Cursors - Complete Guide

Modern applications face a fundamental architectural decision: where should business logic live? In your application code, or inside the database itself? This guide explores the database-side tools that let you embed logic, automate actions, and enforce rules at the data layer.

---

## Stored Procedures: Encapsulating Logic in the Database

A stored procedure is essentially a program that lives inside your database. It's a named collection of SQL statements that you write once, store in the database, and then execute on demand.

### Why Use Stored Procedures?

Imagine you have a complex order processing workflow that involves:
1. Checking inventory levels
2. Calculating discounts based on customer tier
3. Inserting the order record
4. Updating inventory counts
5. Creating entries in an audit log

Without stored procedures, your application would send each of these SQL statements separately over the network. With a stored procedure, you send one command: "execute CreateOrder with these parameters." The database handles everything internally.

**Benefits this provides:**

**Network Efficiency:** Instead of five round trips between your app and database, you make one. For high-latency connections, this can dramatically improve performance.

**Consistency:** The order processing logic is centralized. Every application, every developer, every API endpoint that creates orders uses the exact same logic. No one can accidentally skip the inventory check.

**Security:** You can grant users permission to execute a procedure without giving them direct access to the underlying tables. A user might call `CreateOrder` without having INSERT permissions on the orders table directly.

**Precompilation:** Most databases parse and optimize stored procedures once when created. Subsequent executions skip the parsing phase, providing a modest performance boost.

### A Real Example

Here's a stored procedure that processes a customer order:

```sql
CREATE PROCEDURE ProcessOrder(
    IN customer_id INT,
    IN product_id INT,
    IN quantity INT,
    OUT order_id INT,
    OUT success BOOLEAN
)
BEGIN
    DECLARE available_stock INT;
    DECLARE product_price DECIMAL(10,2);
    
    -- Start transaction for atomicity
    START TRANSACTION;
    
    -- Check if we have enough inventory
    SELECT stock_quantity INTO available_stock
    FROM inventory
    WHERE product_id = product_id
    FOR UPDATE; -- Lock the row
    
    IF available_stock < quantity THEN
        SET success = FALSE;
        ROLLBACK;
    ELSE
        -- Get product price
        SELECT price INTO product_price
        FROM products
        WHERE id = product_id;
        
        -- Create the order
        INSERT INTO orders (customer_id, product_id, quantity, total_price, created_at)
        VALUES (customer_id, product_id, quantity, quantity * product_price, NOW());
        
        SET order_id = LAST_INSERT_ID();
        
        -- Reduce inventory
        UPDATE inventory
        SET stock_quantity = stock_quantity - quantity
        WHERE product_id = product_id;
        
        SET success = TRUE;
        COMMIT;
    END IF;
END;
```

Notice how this handles the transaction logic, error checking, and inventory management all in one atomic unit. Your application just calls:

```sql
CALL ProcessOrder(123, 456, 2, @order_id, @success);
```

### Parameters: IN, OUT, and INOUT

Stored procedures communicate with the outside world through parameters:

**IN parameters** bring data into the procedure. In our example, `customer_id`, `product_id`, and `quantity` are inputs that tell the procedure what order to create.

**OUT parameters** return data from the procedure. The `order_id` and `success` variables are set by the procedure and read by the caller afterward.

**INOUT parameters** (not shown above) can do both—they bring in a value, the procedure modifies it, and the modified value is returned. These are less common but useful for counters or accumulators.

### Control Flow Inside Procedures

Stored procedures support programming constructs like:

**Conditionals:**
```sql
IF stock_level < minimum_threshold THEN
    -- Send reorder alert
END IF;
```

**Loops:**
```sql
WHILE counter < 10 DO
    -- Repeat some operation
    SET counter = counter + 1;
END WHILE;
```

**Error Handling:**
```sql
DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
BEGIN
    ROLLBACK;
    -- Log error
END;
```

This makes stored procedures feel more like traditional programming than simple SQL queries.

---

## Functions vs Procedures: Knowing the Difference

Both functions and stored procedures encapsulate logic, but they serve different purposes and have different capabilities.

### Stored Functions

A function is designed to compute and return a single value. Think of it like a mathematical function—it takes inputs and produces an output.

```sql
CREATE FUNCTION CalculateDiscount(
    customer_tier VARCHAR(20),
    order_amount DECIMAL(10,2)
)
RETURNS DECIMAL(10,2)
BEGIN
    DECLARE discount DECIMAL(10,2);
    
    IF customer_tier = 'GOLD' THEN
        SET discount = order_amount * 0.15;
    ELSEIF customer_tier = 'SILVER' THEN
        SET discount = order_amount * 0.10;
    ELSE
        SET discount = order_amount * 0.05;
    END IF;
    
    RETURN discount;
END;
```

The key advantage? You can use functions directly in SELECT statements:

```sql
SELECT 
    order_id,
    total_amount,
    CalculateDiscount(customer.tier, total_amount) AS discount,
    total_amount - CalculateDiscount(customer.tier, total_amount) AS final_price
FROM orders
JOIN customers ON orders.customer_id = customers.id;
```

### The Critical Differences

**Return Values:**  
Functions must return exactly one value. Procedures can return zero, one, or many values through OUT parameters, or no values at all.

**Usage in Queries:**  
Functions can appear anywhere a value can appear—in SELECT clauses, WHERE conditions, computed columns. Procedures cannot. You must call a procedure as a standalone statement.

**Side Effects:**  
Functions are typically expected to be deterministic and side-effect-free (though this is not always enforced). They shouldn't modify data. Procedures explicitly exist to cause side effects—inserting, updating, deleting data, committing transactions.

**Transaction Control:**  
Functions cannot contain transaction control statements (COMMIT, ROLLBACK). Procedures can and often do.

### When to Use Each

**Use Functions for:**
- Calculations that you need in multiple queries
- Transforming data (format conversions, string manipulation)
- Any logic you want to embed in SELECT statements
- Reusable formulas (tax calculations, distance formulas, etc.)

**Use Procedures for:**
- Multi-step business processes
- Data modifications
- Batch operations
- Anything requiring transaction management
- Operations with multiple output values

---

## Triggers: Automatic Actions on Data Changes

Triggers are the database's way of saying "whenever X happens to this table, automatically do Y." They execute automatically in response to INSERT, UPDATE, or DELETE operations.

### Why Triggers Matter

Triggers shine when you need to enforce rules or maintain consistency that would be tedious or error-prone to handle in application code.

**Scenario:** Every time someone updates a customer's credit limit, you want to log who made the change, when, what the old limit was, and what the new limit is.

Without triggers, every piece of code that updates credit limits must remember to also insert into the audit log. One forgetful developer, one missed code path, and your audit trail has gaps.

With a trigger, it's impossible to update credit limits without creating an audit record. The database enforces this automatically.

### Anatomy of a Trigger

```sql
CREATE TRIGGER audit_credit_limit_changes
AFTER UPDATE ON customers
FOR EACH ROW
BEGIN
    IF OLD.credit_limit <> NEW.credit_limit THEN
        INSERT INTO credit_limit_audit (
            customer_id,
            old_limit,
            new_limit,
            changed_by,
            changed_at
        ) VALUES (
            NEW.customer_id,
            OLD.credit_limit,
            NEW.credit_limit,
            CURRENT_USER(),
            NOW()
        );
    END IF;
END;
```

Let's break this down:

**`AFTER UPDATE ON customers`:** This trigger fires after an UPDATE statement runs on the customers table.

**`FOR EACH ROW`:** The trigger executes once for each row modified by the UPDATE. If you update 100 customers, the trigger fires 100 times.

**`OLD` and `NEW`:** Special references that let you access the data before and after the change. For INSERT, only NEW exists. For DELETE, only OLD exists. For UPDATE, both are available.

The condition `IF OLD.credit_limit <> NEW.credit_limit` ensures we only log when the credit limit actually changed, not when some other column was updated.

### BEFORE vs AFTER Triggers

**BEFORE Triggers** fire before the database performs the operation. This means:

- You can modify the values being inserted or updated by changing NEW
- You can prevent the operation from happening (by signaling an error)
- The operation hasn't happened yet, so you can validate or transform data

Example use case: Enforce a business rule that email addresses must be lowercase.

```sql
CREATE TRIGGER enforce_lowercase_email
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    SET NEW.email = LOWER(NEW.email);
END;
```

**AFTER Triggers** fire after the operation completes. At this point:

- The data is already in the table
- You can't modify the inserted/updated values
- Perfect for auditing, notifications, or updating related tables

Example use case: When inventory drops below threshold, create a reorder request.

```sql
CREATE TRIGGER check_inventory_level
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    IF NEW.quantity < NEW.reorder_threshold THEN
        INSERT INTO reorder_requests (product_id, quantity_needed, created_at)
        VALUES (NEW.product_id, NEW.reorder_threshold - NEW.quantity, NOW());
    END IF;
END;
```

### Practical Trigger Use Cases

**Auditing Every Change:**  
Maintain a complete history of who changed what and when. This is often legally required for financial or medical data.

**Denormalization Maintenance:**  
If you've denormalized data for performance (like caching an order count on each customer record), triggers can keep it synchronized. When a new order is inserted, increment the customer's order count.

**Derived Column Calculations:**  
Automatically compute values based on other columns. When inserting an order line item, calculate and store the line total (quantity × price).

**Enforcing Complex Constraints:**  
CHECK constraints are limited. Triggers can enforce arbitrarily complex rules. For example: "Employees in department X cannot have salaries above $Y."

**Cascading Actions:**  
When an order is canceled, automatically return items to inventory and notify the shipping department.

**Integration Points:**  
Triggers can insert into queue tables that other systems monitor, effectively creating a simple event-driven architecture within the database.

---

## Building an Audit System with Triggers

Let's design a complete audit system that tracks all changes to an employee table.

### The Audit Table

First, we need a place to store the audit trail:

```sql
CREATE TABLE employee_audit (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    operation VARCHAR(10) NOT NULL, -- 'INSERT', 'UPDATE', 'DELETE'
    old_values JSON,
    new_values JSON,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_employee (employee_id),
    INDEX idx_timestamp (changed_at)
);
```

Using JSON columns lets us store the complete before/after state without defining columns for every field.

### The Triggers

**For Inserts:**
```sql
CREATE TRIGGER audit_employee_insert
AFTER INSERT ON employees
FOR EACH ROW
BEGIN
    INSERT INTO employee_audit (employee_id, operation, new_values, changed_by)
    VALUES (
        NEW.employee_id,
        'INSERT',
        JSON_OBJECT(
            'name', NEW.name,
            'department', NEW.department,
            'salary', NEW.salary,
            'hire_date', NEW.hire_date
        ),
        CURRENT_USER()
    );
END;
```

**For Updates:**
```sql
CREATE TRIGGER audit_employee_update
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO employee_audit (employee_id, operation, old_values, new_values, changed_by)
    VALUES (
        NEW.employee_id,
        'UPDATE',
        JSON_OBJECT(
            'name', OLD.name,
            'department', OLD.department,
            'salary', OLD.salary
        ),
        JSON_OBJECT(
            'name', NEW.name,
            'department', NEW.department,
            'salary', NEW.salary
        ),
        CURRENT_USER()
    );
END;
```

**For Deletes:**
```sql
CREATE TRIGGER audit_employee_delete
AFTER DELETE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO employee_audit (employee_id, operation, old_values, changed_by)
    VALUES (
        OLD.employee_id,
        'DELETE',
        JSON_OBJECT(
            'name', OLD.name,
            'department', OLD.department,
            'salary', OLD.salary
        ),
        CURRENT_USER()
    );
END;
```

Now every change to the employees table is automatically recorded with full before/after data. You can query the audit table to see who changed what and when:

```sql
-- See all salary changes for employee 123
SELECT 
    changed_at,
    changed_by,
    JSON_EXTRACT(old_values, '$.salary') AS old_salary,
    JSON_EXTRACT(new_values, '$.salary') AS new_salary
FROM employee_audit
WHERE employee_id = 123 
  AND operation = 'UPDATE'
  AND JSON_EXTRACT(old_values, '$.salary') <> JSON_EXTRACT(new_values, '$.salary')
ORDER BY changed_at DESC;
```

---

## Cursors: When You Need Row-by-Row Processing

Most SQL operations work on entire sets of data at once. You UPDATE all matching rows, INSERT multiple records, SELECT thousands of results. This set-based approach is what makes SQL powerful and fast.

But occasionally you need to process data one row at a time, applying logic that's too complex for a single UPDATE statement. That's where cursors come in.

### What Is a Cursor?

A cursor is essentially a pointer that lets you iterate through a result set one row at a time. Think of it like a for-loop over query results.

### A Complete Cursor Example

Suppose you need to process pending orders, where each order requires calling external logic or multiple conditional steps:

```sql
CREATE PROCEDURE ProcessPendingOrders()
BEGIN
    -- Declare variables to hold column values
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE v_order_id INT;
    DECLARE v_customer_id INT;
    DECLARE v_amount DECIMAL(10,2);
    
    -- Declare the cursor
    DECLARE order_cursor CURSOR FOR
        SELECT order_id, customer_id, amount
        FROM orders
        WHERE status = 'PENDING'
        ORDER BY created_at;
    
    -- Declare handler for when we reach the end
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Open the cursor
    OPEN order_cursor;
    
    -- Loop through results
    order_loop: LOOP
        -- Fetch next row into variables
        FETCH order_cursor INTO v_order_id, v_customer_id, v_amount;
        
        -- Exit if no more rows
        IF done THEN
            LEAVE order_loop;
        END IF;
        
        -- Process this specific order
        -- (Complex logic that can't be expressed in a single UPDATE)
        IF v_amount > 1000 THEN
            -- Large order requires special approval
            UPDATE orders 
            SET status = 'NEEDS_APPROVAL'
            WHERE order_id = v_order_id;
            
            -- Log for manual review
            INSERT INTO approval_queue (order_id, reason)
            VALUES (v_order_id, 'High value order');
        ELSE
            -- Regular order can be processed
            UPDATE orders
            SET status = 'PROCESSING'
            WHERE order_id = v_order_id;
            
            -- Trigger fulfillment
            INSERT INTO fulfillment_queue (order_id)
            VALUES (v_order_id);
        END IF;
        
    END LOOP;
    
    -- Clean up
    CLOSE order_cursor;
END;
```

### The Cursor Lifecycle

**1. Declaration:** You declare the cursor with a SELECT statement that defines what rows it will iterate over.

**2. Opening:** `OPEN cursor_name` executes the query and prepares the result set.

**3. Fetching:** `FETCH cursor_name INTO variables` retrieves the next row and populates your variables.

**4. Processing:** You do whatever logic you need with the current row's data.

**5. Looping:** Repeat fetch and process until no rows remain.

**6. Closing:** `CLOSE cursor_name` releases resources.

### The Handler Pattern

The `DECLARE CONTINUE HANDLER FOR NOT FOUND` line deserves explanation. When FETCH can't retrieve another row (because you've reached the end), it raises a NOT FOUND condition. The handler catches this and sets your `done` flag, letting you exit the loop gracefully.

Without this handler, reaching the end would cause an error.

### Why Cursors Are Slow

Cursors process data row-by-row, which is inherently slower than set-based operations. Here's why:

**Loss of Optimization:** The database optimizer can't see the big picture. When you UPDATE 1000 rows with one statement, the optimizer can plan efficiently. With a cursor updating one row at a time, each update is planned independently.

**Context Switching:** Moving data from the cursor into variables and back to tables involves overhead.

**Lost Parallelism:** Set-based operations can leverage parallel execution. Cursor operations are inherently sequential.

**Network Round Trips:** If the cursor is in client code (not a stored procedure), each fetch is a network round trip.

### When to Actually Use Cursors

Despite their performance cost, cursors are sometimes necessary:

**Complex Per-Row Logic:** When each row requires different processing based on values that can't be determined with CASE statements or other SQL constructs.

**External System Calls:** If processing each row involves calling an external API or sending an email, a cursor makes sense.

**Ordered Processing with Interdependencies:** When row N's processing depends on the result of processing row N-1.

**Small Result Sets:** If you're only processing dozens of rows, the performance hit is negligible.

**Batch Jobs:** Nightly maintenance tasks often use cursors because throughput isn't as critical.

### The Set-Based Alternative

Whenever possible, replace cursor logic with set-based operations:

**Instead of this cursor:**
```sql
DECLARE CURSOR c FOR SELECT id FROM orders WHERE status = 'PENDING';
-- Loop through, updating each order
```

**Use this:**
```sql
UPDATE orders
SET status = 'PROCESSING', processed_at = NOW()
WHERE status = 'PENDING';
```

Always ask: "Can I express this logic as a single UPDATE/INSERT/DELETE?" If yes, avoid the cursor.

---

## The Dark Side: When Database Logic Becomes a Problem

Stored procedures, triggers, and cursors are powerful, but they can create serious problems if overused.

### Vendor Lock-In

SQL is standardized, but stored procedure syntax is not. PL/SQL (Oracle), T-SQL (SQL Server), PL/pgSQL (PostgreSQL), and MySQL's procedure language are all different. A stored procedure written for one database won't run on another without significant rewriting.

If you embed too much logic in procedures, migrating to a different database becomes a massive undertaking. Your "portable" SQL application is now married to a specific vendor.

### Maintenance Nightmares

Application code lives in version control with proper tooling—IDEs, debuggers, unit tests, code review. Database code often gets neglected:

- **No IDE support:** Writing procedures in SQL clients is painful compared to modern development environments.
- **Difficult debugging:** Setting breakpoints and stepping through database code is clunky at best.
- **Version control challenges:** Database objects aren't files, so integrating them into Git requires extra tooling.
- **Testing difficulty:** Unit testing database logic requires database setup, making tests slower and more brittle.

### The Split-Brain Problem

When business logic lives partly in application code and partly in the database, understanding the complete flow becomes difficult. A developer looking at the application might not realize a trigger is modifying data behind the scenes. Debugging becomes archaeological work.

### Performance Surprises

**Trigger Cascade:** One trigger fires another, which fires another. What looks like a simple INSERT becomes a cascade of hidden operations. Performance degrades mysteriously.

**Locking Issues:** Triggers hold transactions open longer, increasing the chance of deadlocks and blocking.

**Cursor Overhead:** As discussed, cursors kill performance. When hidden inside procedures, they're hard to identify as bottlenecks.

### When Database Logic Makes Sense

Despite these concerns, there are legitimate use cases:

**Data Integrity and Auditing:** This is where database logic excels. Audit triggers ensure you never forget to log changes. Validation triggers enforce rules that must never be violated.

**Database-Level Constraints:** Complex constraints that can't be expressed with CHECK or FOREIGN KEY constraints might need triggers.

**Simple Automation:** Automatically updating a timestamp, calculating a derived column, or maintaining a denormalized counter.

**Stored Procedures for Security:** Granting EXECUTE permission on procedures while restricting direct table access gives fine-grained control.

### The Balance: A Practical Approach

Here's a reasonable division of responsibilities:

**Put in the Database:**
- Auditing (triggers)
- Simple data validation (triggers, constraints)
- Data integrity rules that must always hold
- Performance-critical operations where network latency matters

**Keep in Application Code:**
- Complex business workflows
- Integration with external systems
- Anything requiring libraries or frameworks
- Logic that changes frequently
- Operations requiring extensive error handling

**Document Everything:**
- If you write a trigger, comment it thoroughly
- Keep procedure definitions in version control
- Document which tables have triggers and what they do
- Make hidden database logic visible to developers

---

## Practical Wisdom: Using These Tools Effectively

### Start Conservative

Begin with simple stored procedures for clearly defined operations. Don't jump into complex trigger cascades until you understand the implications.

### Measure Performance

Before optimizing with cursors or complex procedures, profile your queries. Often the bottleneck is a missing index, not the need for custom logic.

### Think About the Developer Experience

Every trigger you add is hidden complexity for future developers (including yourself). Is the benefit worth the cognitive overhead?

### Consider Alternatives

Modern application frameworks often provide better ways to handle cross-cutting concerns like auditing (middleware, decorators, aspect-oriented programming).

### Document the "Why"

When you create a trigger or procedure, document not just what it does but why it exists. Future you will be grateful.

---

## Conclusion: Database Logic as a Precision Tool

Stored procedures, triggers, and cursors are sharp tools. Used precisely for the right tasks, they enforce consistency, improve security, and simplify complex operations. Overused, they create maintenance burden, vendor lock-in, and performance problems.

The key is knowing when to reach for these tools and when to leave logic in your application layer. Database logic should feel like strategic placement of guardrails and automation, not like moving your entire application into SQL.

Use these features to make your database smarter about data integrity and automation. But keep your business logic where it's testable, maintainable, and portable.