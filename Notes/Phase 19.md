# 🚀 Phase 19 — Object Database Standards, Languages & Design
Native object storage, querying, and design

Phase 19 builds on object-relational systems and dives fully into object databases (ODBMS), where data is treated as objects natively, rather than being forced into relational tables. This is essential for applications with complex, interrelated data and heavy use of OOP paradigms.

## 1️⃣ Overview of the Object Model (ODMG)
ODMG (Object Database Management Group) defined standards for object databases in the 1990s.

Key points:
- Objects = encapsulation of data + behavior 
- Supports inheritance, polymorphism, aggregation 
- Persistent objects stored in DB just like in memory 
- Ensures standardized object storage, query, and language bindings

Benefits:
- Reduces object-relational impedance mismatch 
- Enables seamless integration with OOP languages

## 2️⃣ Object Definition Language (ODL)
- Used to define the schema of an object database 
- Defines object types, attributes, relationships, and methods

Example:
```postgresql
interface Customer {
attribute string name;
attribute string email;
relationship Set<Order> orders inverse Order.customer;
};
```
- Think of it as DDL for objects 
- Maps closely to OOP concepts (classes, attributes, relationships)

## 3️⃣ Object Query Language (OQL)
- SQL-equivalent for object databases 
- Supports navigating object relationships, collections, and inheritance

Example:
```postgresql
SELECT c.name, o.amount
FROM Customer c, c.orders o
WHERE o.date > '2025-01-01';
```
- Queries are object-oriented, not table-oriented 
- Supports methods on objects in queries

## 4️⃣ Overview of C++ Language Binding
- ODMG provided language bindings for C++, Java, Smalltalk 
- Persistent objects could be used directly in OOP code 
- Example:
```postgresql
Customer* c = db->get<Customer>(1001);
c->addOrder(orderObj);
db->commit();
```
- No need to manually map objects to tables 
- Simplifies complex applications like CAD, simulations, multimedia

## 5️⃣ Object Database Conceptual Design
- Object databases are designed like UML models 
- Key steps:
  - Identify objects (classes) → entities in the domain 
  - Define attributes → properties of objects 
  - Define relationships → associations, aggregation, composition 
  - Define inheritance hierarchy → superclasses & subclasses 
  - Map methods → behavior tied to objects

Example Conceptual Model:
```postgresql
Class: Customer
    Attributes: id, name, email
    Methods: addOrder(), getOrders()
Class: Order
    Attributes: orderId, amount, date
    Relationships: Customer owns Orders
```
- Focus is on modeling the real-world domain naturally

## 🎯 Phase 19 Key Takeaways
1. ODBMS store objects natively, unlike relational tables 
2. ODMG provides standards for object storage, query, and language binding 
3. ODL = schema definition, OQL = object-oriented query language 
4. Language bindings allow objects to be used directly in C++/Java 
5. Conceptual design mirrors OOP class modeling → attributes, methods, relationships 
6. Ideal for applications with complex, interconnected data: CAD, simulation, multimedia