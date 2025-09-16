# Multi-threaded CSV-driven CRUD Processor

**One-line:** A per-user, queue-based, multi-threaded Java system that executes Create / Read / Update / Delete (CRUD) operations from a CSV file into a MySQL database using a lightweight JDBC connection pool.

---

## What this project does
- Reads an **operations CSV**.  
- Groups rows **by user ID** so that each user’s operations remain in sequence.  
- Runs **different users’ operations concurrently** using a fixed-size thread pool (`ExecutorService`).  
- Uses a **custom JDBC connection pool (`SimpleConnectionPool`)** to manage MySQL connections efficiently.  
- Delegates actual DB queries to modular CRUD handler classes: `Create`, `Read`, `Update`, `Delete`.  

---

## Performance highlight 
When processing **40,000+ SQL queries sequentially**, the runtime was:  
- **~9 minutes 8 seconds** with direct connections (no pooling).  
- **~58 seconds** using this system with **custom `SimpleConnectionPool` + `ExecutorService`**, while ensuring that:  
  - **SQL connections were never exhausted** (pool limits respected).  
  - **Concurrency was safe and isolated** (per-user queues).  

---

## Project structure
- **PerUserQueueProcessor.java** *(currently named `BakeryCSVProcessor3.java`)*  
  Main coordinator: parses CSV, builds per-user queues, submits workers to thread pool, calls CRUD handlers.  
- **SimpleConnectionPool.java**  
  Lightweight JDBC connection pool with configurable size and timeout.  
- **Createtable.java**  
  Utility for creating the required MySQL schema.  
- **Create.java / Read.java / Update.java / Delete.java**  
  Modular classes implementing the four CRUD actions.  

---

## How it works
1. **Parse CSV** → Reads header → builds `HEADER_MAP` → parses each row.  
2. **Group by user** → Each user ID gets a `BlockingQueue<Runnable>`.  
3. **Worker assignment** → For each user, submits a worker to a fixed thread pool. Each worker drains that user’s queue sequentially.  
4. **CRUD execution** → Worker gets a connection from `SimpleConnectionPool`, runs the CRUD operation, then releases the connection.  
5. **Shutdown** → Closes all connections gracefully in a JVM shutdown hook.  

This guarantees:  
- **Sequential operations per user**  
- **Concurrent execution across users**  
- **Bounded DB resource usage with pooling**  

---

## Expected CSV schema
The processor expects headers with these fields:
```
Operation, UserID, FullName, Email, PhoneNumber, VisitCount, TotalSpent, Choice, NewValue
```

- `Operation` → `Create | Read | Update | Delete`  
- `UserID` → integer (key for per-user queue)  
- `FullName, Email, PhoneNumber` → user attributes  
- `VisitCount, TotalSpent` → numeric fields  
- `Choice` + `NewValue` → used by `Update` to determine field and new value  

---

## Setup
### Prerequisites
- Java 8+  
- MySQL running locally or remotely  
- `mysql-connector-java` on classpath  

### Steps
```bash
# Compile
javac *.java

# Run table setup
java CreateTable

# Run processor (expects ./operations.csv)
java BakeryCSVProcessor3
```

---

## Improvements to consider
- Rename `BakeryCSVProcessor3` → `PerUserQueueProcessor` (for clarity).  
- Externalize DB credentials (currently hardcoded).  
- Add CSV validation & error handling.  
- Graceful shutdown with `executor.awaitTermination()`.  
- Unit tests with in-memory DB (H2) or Testcontainers.  
- Use a production-grade connection pool (e.g., HikariCP).  

---

## License
MIT / Apache-2.0 (choose one and add a LICENSE file).

