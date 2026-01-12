# 🚀 Phase 20 — XML & Internet Databases
Handling semi-structured data in web and enterprise systems

Phase 20 introduces semi-structured and hierarchical data stored on the web, bridging traditional databases with XML, web services, and modern data interchange formats. This phase is key for understanding Internet-based and document-oriented storage systems.

## 1️⃣ Structured, Semistructured & Unstructured Data
| Type               | Description                                      | Examples                         |
| ------------------ | ------------------------------------------------ | -------------------------------- |
| **Structured**     | Data fits neatly into tables with rows & columns | SQL databases, CSV               |
| **Semistructured** | Partial structure; flexible hierarchy            | XML, JSON, HTML                  |
| **Unstructured**   | No predefined structure                          | Text files, images, audio, video |

💡 Observation: Web and IoT data often falls under semi-structured → requires flexible storage & query mechanisms.

## 2️⃣ XML Hierarchical (Tree) Data Model
- XML stores data as trees of nested elements 
- Each node can have:
  - Elements (child nodes)
  - Attributes (properties)
  - Text content

Example:
```XML
<Customer id="1001">
    <Name>Ahmed</Name>
    <Orders>
        <Order id="5001">100.0</Order>
        <Order id="5002">200.0</Order>
    </Orders>
</Customer>
```
- Tree structure allows nested, hierarchical relationships naturally 
- Ideal for documents, config files, and web services

## 3️⃣ XML Documents, DTD, & XML Schema
### a) XML Document
- Contains data + hierarchical structure

### b) DTD (Document Type Definition)
- Defines allowed elements, attributes, and structure 
- Lightweight, older standard

Example DTD:
```postgresql
<!ELEMENT Customer (Name, Orders)>
<!ATTLIST Customer id ID #REQUIRED>
```
### c) XML Schema (XSD)
- More powerful than DTD → supports:
  - Data types (integer, date, string)
  - Min/max occurrences 
  - Nested structures 
- Preferred in modern systems

Example XSD snippet:
```xml
<xs:element name="Customer">
    <xs:complexType>
        <xs:sequence>
            <xs:element name="Name" type="xs:string"/>
            <xs:element name="Orders" type="OrdersType"/>
        </xs:sequence>
        <xs:attribute name="id" type="xs:int" use="required"/>
    </xs:complexType>
</xs:element>
```
## 4️⃣ XML Documents and Databases
- XML can be stored in native XML databases or relational DBs (shredded into tables)
- Supports queries, indexing, and validation 
- Examples:
  - Native XML DB: eXist-db, MarkLogic 
  - Relational DB + XML: Oracle XML DB, SQL Server XML columns

## 5️⃣ XML Querying (XPath & XQuery intro)
### a) XPath
- Used to navigate and select nodes in an XML tree 
- Example:
```postgresql
/Customer/Orders/Order[@id="5002"]
```
### b) XQuery
- SQL-like query language for XML 
- Can filter, transform, and aggregate XML data 
- Example XQuery:
```postgresql
for $o in /Customer/Orders/Order
where $o/@id = "5002"
return $o
```
- Enables analytics and transformations directly on XML documents

## 🎯 Phase 20 Key Takeaways
1. Not all data is relational → semi-structured data (XML/JSON) is increasingly common 
2. XML uses tree hierarchy, with elements, attributes, and text content 
3. DTD / XSD define structure and data types for validation 
4. XML can be stored natively or in relational DBs 
5. XPath & XQuery enable query and manipulation of hierarchical data 
6. Prepares learners for web-based, document-oriented, and semi-structured databases