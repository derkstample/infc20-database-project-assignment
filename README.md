# Systems Documentation

## Case Description

**Fruity Inc.** is a small-scale company that specializes in arranging and selling unique fruit baskets. Each customer at Fruity Inc. requires a unique account number formatted as `C[0-9][0-9][0-9]` (e.g., `C123`). Customers are associated with a single delivery address and a name, which allows Fruity Inc. to personalize thank-you notes for each delivery.

To manage their offerings, Fruity Inc. assigns each type of fruit basket a unique basket number in the format `B[0-9][0-9]` (e.g., `B01`). Each basket type is associated with a delicious-sounding name and sale price.

Purchases are recorded with no limit on the types of baskets a customer may purchase, nor a limit on the number of customers purchasing a type of basket. Each purchase is associated with a date of purchase in order for Fruity Inc. to properly prioritize their delivery schedule.

---

## Statement of Purpose

This system fulfills Fruity Inc.'s operational and business goals by designing and implementing:

- A **conceptual data model**
  ![conceptualmodel](https://github.com/user-attachments/assets/514cecc6-7853-4ac4-8aac-dd6d5cee3f4d)
- A **logical data model**
  ![logicalmodel](https://github.com/user-attachments/assets/38e0598b-9805-4700-867d-1fb5aeaae3e9)
- A **physical data model**
- A user-friendly **Graphical User Interface (GUI)** with **Create, Read, Update, Delete (CRUD)** functionality
  ![uml](https://github.com/user-attachments/assets/a02063d0-bfd5-4a1a-a7bc-b22651ba41c6)


### Technical Specifications

- **Database**: A Microsoft Azure virtual machine running **Microsoft SQL Server 2022** stores all critical business data.
- **Application**: A **JavaFX application** interacts with the database using the **Java Database Connectivity (JDBC)** driver, structured using:
  - **Model-View-Controller (MVC)** pattern, and
  - **Data Access Object (DAO)** pattern for modular, scalable code

### Security & Data Integrity

- **SQL Stored Procedures & Triggers**: The system uses custom stored procedures and triggers to enforce secure data access and maintain data integrity.
  - **Least Privilege Principle**: Access to the JavaFx application is restricted to calling granted stored procedures.
  - **SQL Injection Mitigation**: CallableStatements in DAOs ensure safe database calls, reducing the risk of SQL injections.
  - **Data Validation**: Triggers enforce format requirements for account and basket numbers, although check constraints on the tables themselves would accomplish the same result.

---

## Additional Information

### Software Used

- **Database & Cloud**: Microsoft Azure, Microsoft SQL Server 2022
- **Management & Development**: SQL Server Management Studio 20, Visual Studio Code
- **Version Control**: Git

### Libraries

No third-party libraries were utilized in this project.

### Known Issues

- **JavaFX Implementation**: The implementation of switching between different views is likely not the most effective one. General JavaFx uncertainties.
  
### Recommendations

- **User Interface Enhancements**: Add visual improvements for greater clarity of information.
- **Error Messages**: Refine for clearer feedback in case of errors.
- **Purchase Table**: The purchase table currently shows very little useful information, expand to add customer names, delivery addresses, etc.
- **Customer & Basket Views**: Add tables to display associated purchases. Existing DAO methods and stored procedures can support this.

---
