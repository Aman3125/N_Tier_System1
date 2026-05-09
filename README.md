







---
title: "GCA2 — N-tier System"
subtitle: "README"
description: "Project overview, setup, protocol, architecture, testing evidence, and contribution matrix for GCA2."
module: "COMP C8Z03 Object-Oriented Programming"
stage: "2 (Group Project)"
generated_at: "2026-02-20 09:00 Europe/Dublin"
---

# 2025-26 - OOP - L8 - GCA2 — N-tier System

## 1. Project Overview

The Garage Management System is a Java client-server application made to help manage a car garage. The system is designed for garage workers and administrators to keep track of customers, vehicles, and service jobs in an organised way. The program uses Java sockets for communication between the client and server, JDBC to connect to the database, and MySQL to store all the information permanently.

The main parts of the system are Customers, Vehicles, and Service Jobs. A customer can own one or more vehicles, and each vehicle can have different service jobs such as repairs, oil changes, diagnostics, or maintenance work. The system allows users to add, view, update, and delete records using a menu-driven client application connected to a multithreaded server.

The project also supports binary file upload and download features. This allows files such as invoices, receipts, repair reports, or vehicle documents to be attached to a service job. The files are stored directly in the database as binary data and can later be downloaded back onto the client computer when needed.

### Team
- **Group ID:** `2025-26-L8-OOP-GCA2-SD2B`
- **Members:**
  - Student A — `Aman Akhtar`
  - Student B — `Abdirahman Farah`

### Key features
- JDBC DAO layer with full CRUD functionality for Customers, Vehicles, and Service Jobs (Stage 1 foundation)
- Client–server architecture using Java sockets with JSON communication and ServerResponse<T> wrapper responses
- Multithreaded server implementation using ExecutorService to handle multiple clients simultaneously
- Binary file upload and download functionality with files stored as BLOB data in the database along with metadata
- JUnit 5 test suite with line coverage analysis and evidence of achieving at least 70% coverage in the final stage
- MySQL relational database integration using JDBC and PreparedStatement for secure SQL operations
- Layered N-Tier architecture separating domain, DAO, networking, and database logic
- Menu-driven console client for interacting with the system
- Validation and error handling for database operations and client-server communication
---

## 2. How to Run

### Prerequisites
- Java JDK 17+
- IntelliJ IDEA (recommended)
- MySQL Server (local installation)
- Maven/Gradle dependencies configured
- Gson library included in project dependencies

### 2.1 Database setup
Create a database:

CREATE DATABASE car_garage_db;

Run the SQL setup script provided in the project.

The script will:

- create the customer, vehicle, and service_job tables
- create foreign key relationships
- insert sample data into all tables
- create indexes for service job status

Verify that:

- all tables are created successfully
- sample data exists in each table
- service_job contains the binary file columns:
- file_data
- file_name
- content_type
- file_size

### 2.2 Configure credentials
Open:

DatabaseConnection.java

Update the database connection details if required:

private static final String URL =
        "jdbc:mysql://localhost:3306/car_garage_db";

private static final String USER = "root";

private static final String PASSWORD = "your_password";

### 2.3 Run the server
Main class:

daoexample.net.GarageServer

Default port:

5050

Expected output:

Server running on port 5050

The server must remain running before starting the client.
The server uses ExecutorService to handle multiple clients simultaneously.

### 2.4 Run the client(s)
Main class:

daoexample.net.GarageClient

The console menu will appear with all available operations.

Example:

=== Garage Client Menu ===
1. Display Customer by ID
2. Display All Customers
3. Display Vehicle by ID

---

## 3. Architecture Summary

### 3.1 N-tier overview
This project follows an N-tier architecture. Each layer has a separate responsibility, which helps keep the system organised and easier to maintain.
- Client layer: GarageClient provides the console menu used by the user. It sends JSON requests to the server and displays the server responses.
- Server layer: GarageServer listens for client connections using sockets. Each client is passed to a ClientHandler, and ExecutorService allows multiple clients to be handled at the same time.
- DAO layer: DAO interfaces define the database operations, while JDBC implementation classes perform the actual SQL queries using PreparedStatement.
- Database layer: MySQL stores the customer, vehicle, and service job data. It also stores uploaded binary files as BLOB data with file metadata.

The main flow is:

Client → JSON Request → Server → DAO → MySQL Database
Client ← JSON Response ← Server ← DAO ← MySQL Database

### 3.2 Architecture diagram
- Path: `docs/architecture.md`
- Diagram format: Mermaid

  <img width="4892" height="621" alt="mermaid-diagram" src="https://github.com/user-attachments/assets/af382855-6a01-49c7-abb5-1c7ab87fa168" />


---

## 4. JSON Protocol Documentation
The system uses a JSON-based protocol between the client and server over TCP sockets.
Each request is sent from the client as a JSON object, and the server responds using a ServerResponse<T> wrapper.

> **Keep this section up to date** as you add request types. Stage 2 requires the protocol to be documented in the README.

### 4.1 Envelope format (example)
- **Request**
{
  "entity": "SERVICEJOB",
  "action": "GET_BY_ID",
  "id": 1,
  "entityData": null
}
- **Response**
{
  "status": "SUCCESS",
  "message": "Service job found",
  "data": {
    "serviceJobId": 1,
    "vehicleId": 2,
    "description": "Brake replacement"
  }
}

### 4.2 Supported request types
| Request Type | Description | Required Fields |
|---|---|---|
| `GET_ALL` | Returns all records for an entity | `entity` |
| `GET_BY_ID` | Returns a single entity by ID | `entity`, `id` |
| `CREATE` | Inserts a new entity into the database | `entity`, `entityData` |
| `UPDATE` | Updates an existing entity | `entity`, `id`, `entityData` |
| `DELETE` | Deletes an entity by ID | `entity`, `id` |
| `UPLOAD_FILE` | Uploads a binary file to a ServiceJob | `entity`, `id`, `base64FileData`, `fileName`, `contentType` |
> Note: Stage 1 filtering is via `Predicate<T>` internally; don’t implement “SQL string filters” per request.


### 4.3 Example Requests

#### Get Customer by ID

```json
{
  "entity": "CUSTOMER",
  "action": "GET_BY_ID",
  "id": 1
}
```

---

#### Create Vehicle

```json
{
  "entity": "VEHICLE",
  "action": "CREATE",
  "entityData": "{ \"customerId\":1, \"make\":\"Toyota\", \"model\":\"Corolla\" }"
}
```

---

#### Upload File to Service Job (F18)

```json
{
  "entity": "SERVICEJOB",
  "action": "UPLOAD_FILE",
  "id": 3,
  "base64FileData": "JVBERi0xLjQKJcfs...",
  "fileName": "receipt.pdf",
  "contentType": "application/pdf",
  "fileSize": 20480
}
```

---

### 4.4 Error Handling

The server always responds using `ServerResponse<T>`.

Example error response:

```json
{
  "status": "ERROR",
  "message": "Service job not found",
  "data": null
}
```

Possible errors include:

- Invalid entity name
- Unsupported action
- Missing ID
- Validation failures
- Database connection errors
- Invalid Base64 file data
- Entity not found

---

## 5. Binary File Handling (Stage 3+)

### 5.1 What binary data represents in our domain
- Example: Player profile image / Evidence photo / Receipt scan / Audio clip

The system stores files related to garage service jobs.  
These files can represent:

- Vehicle repair receipts
- Service invoices
- Diagnostic reports
- Images of vehicle damage
- PDF documents related to repairs

Each uploaded file is linked to a specific `ServiceJob` record in the database.

---

### 5.2 Storage approach

Binary files are stored directly in the MySQL database using a BLOB column.

The `service_job` table includes the following file-related fields:

| Column Name | Type | Purpose |
|---|---|---|
| `file_data` | BLOB | Stores the binary file bytes |
| `file_name` | VARCHAR | Original uploaded file name |
| `content_type` | VARCHAR | MIME type of the file |
| `file_size` | INT | Size of the file in bytes |

The application uses:

- `PreparedStatement.setBytes()` to store binary data
- Base64 encoding for transferring files over JSON
- Base64 decoding on the server before database storage

---

### 5.3 Supported binary operations

| Operation | Request Type | Notes |
|---|---|---|
| Upload file | `UPLOAD_FILE` | Client encodes file to Base64 before sending |
| Store file | `UPDATE ServiceJob` | Server decodes Base64 and stores BLOB |
| Retrieve metadata | `GET_BY_ID` | File metadata returned with ServiceJob |
| Retrieve file data | `GET_BY_ID` | Binary data included in ServiceJob object |

---

### 5.4 Binary Upload Process (F18/F19)

1. User selects a local file path in the client application
2. Client reads the file from disk
3. File is converted into Base64 format
4. JSON request is sent to the server
5. Server decodes the Base64 data into bytes
6. File bytes are stored in MySQL using JDBC
7. Server returns a `ServerResponse<T>` confirming success

---

### 5.5 Example Upload Request

``json
{
  "entity": "SERVICEJOB",
  "action": "UPLOAD_FILE",
  "id": 2,
  "base64FileData": "JVBERi0xLjQKJcfs...",
  "fileName": "repair_receipt.pdf",
  "contentType": "application/pdf",
  "fileSize": 20480
}

---

## 6. Testing & Coverage

### 6.1 Running tests

The project includes a JUnit 5 test suite located in:

```text
src/test/java/daoexample/dao/
```

The test files include:

- `CustomerDaoTest.java`
- `VehicleDaoTest.java`
- `ServiceJobDaoTest.java`
- `DatabaseConnectionTest.java`

To run all tests, use:

```bash
mvn test
```

The tests cover the main DAO operations, including:

- retrieving records by ID
- retrieving all records
- inserting new records with generated IDs
- updating existing records
- deleting records
- JSON conversion tests
- filtering using `Predicate<T>`
- database connection testing
- binary file upload and retrieval testing for `ServiceJob`

The `CustomerDaoTest` checks customer CRUD operations, JSON round-trip conversion, filtering, and boundary cases for invalid IDs.

The `VehicleDaoTest` checks vehicle CRUD operations, JSON conversion, filtering, and invalid ID handling.

The `ServiceJobDaoTest` checks service job CRUD operations, JSON conversion, filtering, binary file storage/retrieval, metadata-only retrieval, and invalid ID handling.

The `DatabaseConnectionTest` verifies that a database connection can be opened and a simple `SELECT 1` query can run successfully.


### 6.2 Coverage evidence (Stage 4)
- Coverage screenshot committed to:

  <img width="1920" height="1080" alt="Screenshot 2026-05-08 155122" src="https://github.com/user-attachments/assets/fcd1d847-edf8-49ec-9ffd-c73da6b378ed" />

- Target:
  - **≥ 70% line coverage** across DAO + JSON + binary handling classes

---

## 7. Design Patterns, Generics, Lambdas

### 7.1 Patterns used

- **DAO Pattern**  
  The project uses the DAO (Data Access Object) pattern to separate database logic from business logic.  
  Classes such as `JdbcCustomerDao`, `JdbcVehicleDao`, and `JdbcServiceJobDao` handle all database operations independently from the networking layer.

- **DTO Pattern**  
  DTOs (Data Transfer Objects) are used to transfer structured data between the client, server, and database layers.  
  Classes such as `Customer`, `Vehicle`, and `ServiceJob` are used as DTOs throughout the application.

- **Client–Server Pattern**  
  The application follows a client–server architecture where the client sends JSON requests and the server processes them and returns JSON responses.

- **Thread Pool Pattern**  
  The multithreaded server uses `ExecutorService` to manage client connections efficiently without blocking the server.

---

### 7.2 Generics usage

The project uses Java generics to improve code reuse and type safety.

Examples include:

- `ServerResponse<T>`  
  Used to wrap all server responses while supporting different DTO types.

- `List<Customer>`
- `List<Vehicle>`
- `List<ServiceJob>`

- `Optional<Customer>`
- `Optional<Vehicle>`
- `Optional<ServiceJob>`

These generic collections and wrappers reduce duplicate code and improve readability.

---

### 7.3 Functional interfaces / lambdas

The project uses Java functional interfaces and lambda expressions for filtering operations.

Example:

```java
Predicate<ServiceJob> filter
```

Used in:

```java
findServiceJobsByFilter(Predicate<ServiceJob> filter)
```

The filtering is implemented using Java Streams:

```java
return getAllServiceJobs().stream()
        .filter(filter)
        .collect(Collectors.toList());
```

This allows flexible filtering logic without creating multiple separate methods.

---

## 8. Screencast (Stage 4)
Abdi was meant to do part 1 of the video but was having troubles so i have done 
and thats why this part 1 video was added late.

- Part 1 = https://github.com/user-attachments/assets/845a4d35-e23e-4dc3-ac83-0116585aaeb6
- Part 2 = https://github.com/user-attachments/assets/cc969226-5410-4bf0-bcfd-02468d6b1181



---

## 9. Contribution Matrix (Required)

> One row per **major task**. “Primary” means who implemented first version. “Contributor/Reviewer” means meaningful review, refactor, debugging, extension, or pair work.

### 9.1 Matrix (example for a 3-person team)

| Major task | Primary author | Contributor / reviewer | Notes |
| :- | :- | :- | :- |
| Domain proposal email (150–200 words) + entity list for approval | Student A | Student B | Drafted + refined before sending |
| Repo setup (private repo, collaborators, branch plan stage1–stage4) | Student A | Student B | Created branches + README skeleton |
| `mysqlSetup.sql` schema + seed data (10+ rows per table) | Student A | Student B | Re-runnable from scratch |
| DTO/entity modelling + validation rules (trim/blank/range checks) | Student A | Student B | Included int/double/string fields |
| DAO interfaces (XxxDao) for all entities | Student A | Student B | Service depends on interfaces only |
| JDBC DAO implementation: `getAll` + `getById` using `Optional<T>` | Student A | Student B | PreparedStatements throughout |
| JDBC DAO implementation: `insert` returning generated keys | Student B | Student A | Verified `getGeneratedKeys()` |
| JDBC DAO implementation: `update` + `deleteById` | Student B | Student A | Consistent return semantics |
| Predicate filtering API (`findByFilter(Predicate<T>)`) | Student B | Student A | Lambda-based filtering |
| JSON conversion (toJson/fromJson/listToJson) per entity | Student B | Student A | Round-trip verified |
| Architecture diagram (Mermaid) + annotated tier explanation | Student A | Student B | Updated as architecture evolved |
| Multithreaded server (`ExecutorService`, client handler per connection) | Student A | Student B | Clean shutdown + logging |
| `ServerResponse<T>` wrapper + consistent response mapping | Student A | Student B | No raw types |
| Protocol documentation in README (all request types + payloads) | Student A | Student B | Kept current per stage |
| Client features: display all + display by id | Student A | Student B | Implemented for owned entity |
| Client features: insert/update/delete over sockets | Student B | Student A | Handles failures gracefully |
| Error handling: structured failures (no stack traces to client) | Student B | Student A | Includes validation + DB errors |
| Binary schema extension (BLOB + metadata columns) | Student A | Student B | Updated `mysqlSetup.sql` |
| Binary upload (Base64 encode/decode + DB storage) | Student A | Student B | Stored bytes + metadata |
| Binary retrieval (reconstruct file on client) | Student A | Student B | Verified bytes match |
| Metadata-only query (no BLOB fetch) | Student B | Student A | Separate DAO method |
| Disconnect protocol (`DISCONNECT`) + cleanup | Student B | Student A | Releases thread cleanly |
| Stage 3 core tests (DAO read, insert+id, JSON round-trip) | Student B | Student A | 3+ tests each |
| Stage 4 extended tests (server scenario + binary scenario + full DAO) | Student B | Student A | Added 3+ more each |
| Coverage evidence screenshot `/reports/coverage.png` | Student A | Student B | IntelliJ coverage runner |
| Screencast (8–10 min): demo + design iterations | Student A | Student B | Script + recording + export |
| Harvard references + AI usage declaration | Student A | Student B | All sources cited |
| Final README polish (run steps, protocol, testing, evidence links) | Student A | Student B | Consistent formatting |
---

## 10. References (Harvard)

- [1] … https://github.com/nmcguinness/L8---OOP---Module-Content/tree/main/notes
- [2] …

---

## 11. AI Tool Use Declaration

- Tools used:
  - …
- What was generated:
  - …
- What was modified by the team:
  - …
