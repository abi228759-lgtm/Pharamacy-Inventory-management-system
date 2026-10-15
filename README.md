Pharmacy Inventory Management System
A centralized web application that helps a pharmacy manage medicines, stock, expiry dates, billing, suppliers and users in one secure place.
Problem Statement

Pharmacies still depend on disconnected manual records, which leads to:

Stockouts when demand increases
Expiry and wastage because medicines expire before they are sold
Billing discrepancies from wrong quantities and duplicate entries
Supplier delays that interrupt availability
Batch tracking difficulties
Overstocking that ties up money and storage space

Aim: develop a centralized system that improves medicine stock accuracy, reduces avoidable wastage and supports timely purchasing decisions.

Technologies Used
Layer	Technology
Language	Java 21
Backend	Spring Boot 4.1.1, Spring MVC, Spring Data JPA (Hibernate), Spring Security, Bean Validation
Frontend	Thymeleaf, HTML, CSS, JavaScript
Database	H2 (file database, default) or MySQL (optional)
Build tool	Maven (Maven Wrapper included)
Main Features

Inventory

Add, edit, delete and search medicines (name, category, batch, quantity, reorder level, expiry date, price)
Filter by status: Expired, Expiring soon, Low stock
Import medicines from a CSV file and export the current list to CSV

Expiry and stock alerts

Expired medicines highlighted in red
Medicines expiring within 30 days highlighted in orange
Low-stock medicines (quantity at or below the reorder level) highlighted in yellow
Dashboard cards and alert tables with total medicines, expired, expiring, low stock, stock value and today's revenue

Billing

Multi-item bills with customer details, discount percentage and automatic GST
Stock is reduced automatically in one database transaction, so an invoice and its stock deduction succeed or roll back together
Expired medicines and quantities above available stock are blocked
Printable invoice with an invoice number and invoice history
Quick single-item sale page

Other modules

Supplier management
Rule-based chatbot that answers from live data (stock, price, expiry, low stock, today's sales)
Feedback and ratings (admin can view and delete feedback)
User management for admins (create users, activate or deactivate)
REST API with JSON responses and proper error messages

Security

Login with hashed passwords (BCrypt)
Role-based access: Admin, Pharmacist, Cashier
CSRF protection on all web forms
Roles and Access
Role	Access
Admin	Everything, including user management and viewing feedback
Pharmacist	Medicines, suppliers, billing, quick sale, dashboard, feedback
Cashier	Billing, quick sale, dashboard, feedback
Installation and Execution
Prerequisites
JDK 21 (check with java -version)
Internet connection on the first run (Maven downloads dependencies)
Optional: VS Code with Extension Pack for Java and Spring Boot Extension Pack
Steps
Get the project
   git clone <your-repository-url>
   cd <repository-folder>

Make sure you are in the folder that contains pom.xml.

Run the application Windows (PowerShell):
   .\mvnw.cmd spring-boot:run

macOS / Linux:

   ./mvnw spring-boot:run
Open the app at http://localhost:8080 once you see Started PharmacyApplication in the terminal.
Log in with a demo account:
Role	Email	Password
Admin	admin@pharmacy.com	admin123
Pharmacist	pharmacist@pharmacy.com	pharma123
Cashier	cashier@pharmacy.com	cashier123

These are demo credentials. Change them (see application.properties and SampleDataLoader.java) before using the system for anything real.

Demo data

On first start the app loads sample medicines (from src/main/resources/data/medicines.csv), suppliers, invoices, feedback and users, so every page has data. To start with a clean database, stop the app, delete the data/ folder and run again.

Using MySQL instead of H2
Create a MySQL server and make sure it is running.
In src/main/resources/application.properties, comment out the H2 lines and use:
   spring.datasource.url=jdbc:mysql://localhost:3306/pharmacy?createDatabaseIfNotExist=true&serverTimezone=Asia/Kolkata
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
Restart the app. Tables are created automatically.
Importing medicines from CSV

On the Medicines page use Import from CSV. Columns: Medicine, Category, Batch Code, Quantity, Reorder Level, Expiry Date (yyyy-MM-dd), Price. Only Medicine and Price are required, and names that already exist are skipped.

REST API

All endpoints use HTTP Basic authentication (Admin or Pharmacist for medicines; Admin, Pharmacist or Cashier for sales).

Method	Endpoint	Description
GET	/api/medicines	List all medicines
GET	/api/medicines/{id}	Get one medicine
POST	/api/medicines	Create a medicine
PUT	/api/medicines/{id}	Update a medicine
DELETE	/api/medicines/{id}	Delete a medicine
GET	/api/medicines/summary	Totals, expired, expiring soon, low stock, stock value
GET	/api/sales	Recent sales
POST	/api/sales	Sell a medicine ({"medicineId":1,"quantity":2})

Example:

curl -u admin@pharmacy.com:admin123 http://localhost:8080/api/medicines/summary

Project Structure

src/main/java/com/pharmacy/
├── api/          REST controllers and API error handling
├── config/       Security configuration, demo data loaders
├── controller/   Web (MVC) controllers
├── entity/       JPA entities (Medicine, Invoice, Supplier, User, ...)
├── repository/   Spring Data JPA repositories
└── service/      Business logic (billing, chatbot, import, ...)
src/main/resources/
├── templates/    Thymeleaf pages
├── static/css/   Stylesheets
├── data/         medicines.csv (demo data)
└── application.properties
Future Enhancements

The following are planned but not yet implemented:

Purchase orders, supplier price comparison and order tracking with partial receipt
Reorder-point suggestions based on average daily sales, lead time and safety stock
30 / 60 / 90-day expiry alert levels
Stock reconciliation (physical count vs system quantity)
Audit logs for sensitive changes
Optional OTP / two-step verification
Sandbox payment gateway integration
Barcode scanning and invoice import
Sales and stock reports with charts

References

Spring Boot Documentation: https://docs.spring.io/spring-boot/                                         
Spring Data JPA Reference: https://docs.spring.io/spring-data/jpa/reference/                                    
MySQL Reference Manual: https://dev.mysql.com/doc/                                               
OWASP Cheat Sheet Series: https://cheatsheetseries.owasp.org/                                                            
World Health Organization (WHO),good storage and distribution practices for medical products: https://www.who.int/
