# PostgreSQL-ProductManagement
This Java program is a GUI-based product management system that interacts with a PostgreSQL database to manage inventory, sales, and product information.

SYSTEM ARCHITECTURE
Key components of the system include the user interface for handling input and
displaying information, the JDBC layer for database communication, and the database
layer for securely storing product data. Data flows between the user interface and
database via the JDBC API, processing requests and returning results for display. The
system will be deployed locally in a standalone configuration, connecting to the
PostgreSQL database using local host settings.

<img width="1396" height="766" alt="image" src="https://github.com/user-attachments/assets/7e31f763-7163-48a9-b40f-0ae70c2163c5" />




DESIGN SPECIFICATION
The system prioritizes modularity and scalability for future enhancements, featuring key
modules like Product Management for CRUD operations and User Authentication for
access control. Main classes include Product, User, and Inventory, with a database
structured for referential integrity. Sequence diagrams illustrate the UI-to-backend data
flow during product addition, while state diagrams track product statuses like Available
and Out of Stock. The system employs the JDBC API for executing SQL statements and
data retrieval.

<img width="1325" height="749" alt="image" src="https://github.com/user-attachments/assets/90dbb5a0-5a02-42f7-97e6-162f2a36ab05" />

