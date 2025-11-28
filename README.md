# Order Management System (OMS) - HotWax Commerce Assignment

## 📌 Overview
This project is a RESTful API solution developed for the **HotWax Commerce Technical Round 2**. 

It is a pure Java implementation of an Order Management System that handles complex order processing logic, including **atomic transactions**, **cascading deletes**, and **inventory management**, without relying on high-level frameworks like Spring Boot. This approach demonstrates a deep understanding of core Java, JDBC, and Servlet architecture.

## 🚀 Features
The API supports the following operations based on the assignment requirements:

- **Create Order:** Creates an order and multiple line items in a single **atomic transaction**. If one item fails, the entire order rolls back.
- **Retrieve Order:** Fetches full order details including Customer, Shipping/Billing addresses, and all Order Items.
- **Update Order:** Updates Shipping and Billing contact mechanisms.
- **Delete Order:** Deletes an order and automatically removes associated items (Cascading Delete).
- **Manage Items:** Add new items to an existing order, update item quantities, or delete specific items.

## 🛠️ Tech Stack
- **Language:** Java 17
- **Database:** MySQL 8.0
- **Build Tool:** Maven (Wrapper included)
- **Web Server:** Jetty (Embedded via Maven)
- **Architecture:** Servlets & JDBC (Raw SQL)
- **Libraries:**
  - `javax.servlet-api` (REST Endpoints)
  - `mysql-connector-j` (Database Connectivity)
  - `gson` (JSON Parsing)

## ⚙️ Setup & Installation

### Prerequisites
-
