# EcoCycle

A web-based waste management and recycling marketplace. People sell recyclable waste to registered recycling companies, and the companies sell recycled products back through the same platform. An admin approves companies and controls pricing and commission.

> Payments are **simulated**: they are recorded in the database and no real payment gateway is integrated.

## Features

**Users**
- Register and log in
- Sell waste: choose a type, enter the weight, see the estimated price, and submit a pickup request
- Track each request: Submitted → Accepted → Picked Up → Paid (or Cancelled)
- See payments received
- Browse, search and filter recycled products, then use the cart and checkout
- View order history and review products they have bought

**Companies**
- Register, then log in once the admin has approved the account
- Browse open waste requests (filter by type and city) and accept them; only one company can win a request
- Mark pickups and record the payment to the user
- Add, edit and remove products with pictures
- Sales dashboard: units sold, revenue, platform commission and net earnings
- Read customer reviews

**Admin**
- Approve or reject company registrations; block or unblock users and companies
- Set the per-kg rate for each waste type and the platform commission percentage
- Statistics dashboard
- Monitor waste requests, payments, orders and products
- Remove inappropriate products and delete abusive reviews

## Tech stack

| Layer | Technology |
|---|---|
| Presentation | JSP, JSTL/EL, HTML5, CSS3, a little JavaScript |
| Controller | Java Servlets (Jakarta EE 10) |
| Data access | JDBC with the DAO pattern |
| Database | Oracle Database Express Edition |
| Server | Apache Tomcat 10.1 |
| Build | Maven, JDK 21 |

The app follows the **MVC** pattern: servlets are controllers, JSP pages are views, and model and DAO classes handle data and database access.

## Project structure

```
src/main/java/com/ecocycle/
  controller/   servlets (one per page or action)
  dao/          database access
  model/        plain data classes
  service/      logic shared by several servlets (cart)
  filter/       AuthFilter: login and role checks
  util/         DBUtil, PasswordUtil, Validator, ImageStorage, ...
src/main/webapp/
  WEB-INF/views/  JSP pages (not directly reachable from the browser)
  css/            stylesheet
database/         schema.sql and seed.sql
```

## Business rules

1. Waste price = weight × the per-kg rate of the chosen type. The rate in force at submission is saved on the request.
2. A waste request can be accepted by only one company.
3. On every product sale the platform keeps a commission percentage, saved on each order item at the time of the sale.
4. Only users who bought a product can review it.
5. Companies cannot accept waste or list products until the admin approves them.
6. Payments are simulated.

## Security

- Passwords are hashed with BCrypt
- Every database query uses prepared statements
- Role-based access control with a servlet filter; blocked accounts lose access immediately
- Server-side validation on all forms, plus checks on uploaded images (real file type, size limit, server-chosen file names)
- Prices, amounts and commission are always calculated on the server

## Setup

### Requirements

- JDK 21
- Apache Tomcat 10.1
- Oracle Database Express Edition (developed and tested on 10g XE)
- Maven (bundled with NetBeans)

### 1. Create the database user

Connect as `SYSTEM` and run:

```sql
CREATE USER ecocycle IDENTIFIED BY choose_a_password
  DEFAULT TABLESPACE users QUOTA UNLIMITED ON users;
GRANT CONNECT, RESOURCE TO ecocycle;
```

### 2. Create the tables

Connect as `ecocycle` and run the scripts in this order:

```
@database/schema.sql
@database/seed.sql
```

### 3. Configure the connection

Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties` and fill in your password. This file is listed in `.gitignore` and must never be committed.

```properties
db.url=jdbc:oracle:thin:@localhost:1521:XE
db.user=ecocycle
db.password=your_password_here
```

On a newer Oracle version, use the service-name URL form (`jdbc:oracle:thin:@localhost:1521/XEPDB1`) and a matching driver version in `pom.xml`.

### 4. Create the admin account

Passwords are stored as BCrypt hashes, so the admin account is created with a hash:

1. Run `com.ecocycle.util.GenerateHash` (in NetBeans: right-click the file, then Run File) and type a password.
2. Insert the admin, pasting the printed hash:

```sql
INSERT INTO admins (full_name, email, password_hash)
VALUES ('Site Admin', 'admin@ecocycle.com', 'paste_the_hash_here');
COMMIT;
```

### 5. Build and run

```
mvn clean package
```

Deploy `target/ecocycle-1.0-SNAPSHOT.war` to Tomcat 10.1, or run the project from NetBeans. Then open `http://localhost:8080/ecocycle/` (use the port your Tomcat runs on).

Uploaded product pictures are stored outside the application, in `ecocycle-uploads/products` inside the user's home folder.

## Roles and entry points

| Role | Login page |
|---|---|
| User | `/login` |
| Company | `/company-login` |
| Admin | `/admin-login` (linked in the footer of the home page) |

## Possible future work

- Order shipping and delivery status updates
- Password reset by email
- Pagination for the long admin lists
- Automated tests