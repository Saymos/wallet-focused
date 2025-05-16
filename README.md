# Wallet Service API

A Java Spring Boot wallet service that implements basic bookkeeping (accounting) functionality for tracking funds, similar to a bank account. The service is built with a focus on correctness, thread safety, and clear API design.

## Features

- Double-entry bookkeeping for all transactions
- Thread-safe concurrent transfers with deadlock prevention
- Idempotent operations using transaction IDs
- Comprehensive validation and error handling
- API documentation via Swagger UI
- Virtual threads support (Java 21)

## Technology Stack

- Java 21
- Spring Boot 3.4.5
- Spring Validation
- SLF4J/Logback for logging
- Springdoc OpenAPI for API documentation
- JUnit 5 and Mockito for testing

## Build and Run Instructions

### Prerequisites

- Java 21 JDK
- Maven 3.8+

### Building the Application

```bash
# Clone the repository (if not done already)
git clone https://github.com/yourusername/wallet-focused.git
cd wallet-focused

# Build the application
mvn clean package
```

### Running the Application

```bash
# Run using Maven
mvn spring-boot:run

# Or run the JAR directly
java -jar target/wallet-focused-0.1.0-SNAPSHOT.jar
```

The application will start on port 8080 by default.

## API Documentation

Swagger UI is available at: http://localhost:8080/swagger-ui.html

The API consists of three main endpoints:

1. **Get Account Balance**: `GET /api/v1/accounts/{id}/balance`
2. **Transfer Funds**: `POST /api/v1/accounts/transfer`
3. **List Transactions**: `GET /api/v1/accounts/{id}/transactions`

### Example API Requests

#### Get Balance

```bash
curl -X GET http://localhost:8080/api/v1/accounts/{accountId}/balance
```

#### Transfer Funds

```bash
curl -X POST http://localhost:8080/api/v1/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "123e4567-e89b-12d3-a456-426614174000",
    "sourceAccountId": "123e4567-e89b-12d3-a456-426614174001", 
    "destinationAccountId": "123e4567-e89b-12d3-a456-426614174002",
    "amount": 100.00
  }'
```

#### List Transactions

```bash
curl -X GET http://localhost:8080/api/v1/accounts/{accountId}/transactions
```

## Design Decisions

### Double-Entry Bookkeeping and Event Sourcing

The wallet service uses a combination of double-entry bookkeeping and event sourcing patterns:

1. **Double-Entry Bookkeeping**
   - Every transfer creates two transaction entries: a DEBIT for the source account and a CREDIT for the destination account
   - Both entries share the same transaction ID to link them together
   - This approach ensures that the ledger is always balanced (total debits = total credits)

2. **Event Sourcing**
   - Account balances are not stored directly, but calculated from the transaction history
   - This provides a complete audit trail and allows for balance reconstruction at any point in time
   - The formula used is: `balance = sum(CREDIT amounts) - sum(DEBIT amounts)`

3. **Event Sourcing + Projection Pattern**
   - While this implementation uses pure event sourcing (derived balances only), in production systems both approaches are often combined:
     - **Derived balance**: The authoritative balance calculated from transaction history (for audits)
     - **Cached balance**: A denormalized balance stored for fast reads (updated during transfers)
     - **Reconciliation job**: Background process that regularly verifies and fixes any discrepancies
   - This pattern balances consistency (event sourcing) with performance (projection)

🧠 **Bonus Insight**: Many financial systems use both approaches simultaneously. They maintain derived balances for authority and audits, while using cached balances for fast reads. The cached balances are regularly reconciled against the derived values to ensure consistency.

### Thread Safety & Concurrency

- Uses Java's `ReentrantLock` for account-level locking
- Implements consistent lock ordering to prevent deadlocks (acquiring locks by UUID comparison)
- All collection classes are thread-safe (ConcurrentHashMap, CopyOnWriteArrayList)
- Virtual threads (Java 21) are used for request handling to improve scalability

### Idempotency

The service guarantees idempotency for transfers using transaction IDs:

- Each transfer has a unique transaction ID provided by the client
- The system tracks processed transaction IDs to ensure a transaction is applied only once
- Duplicate transfer requests with the same transaction ID are safely ignored

### Account Creation

- Accounts are created implicitly when they are the destination of a transfer
- No explicit "create account" endpoint is provided

## Implementation Shortcuts & Production Considerations

This implementation includes several shortcuts that would need to be addressed in a production environment:

### In-Memory Storage

- The current implementation uses in-memory storage (`ConcurrentHashMap`)
- For production, a persistent database with ACID transactions would be required

### Cluster Scalability

- The current locking mechanism works for a single instance but not for multiple instances
- A production solution would require distributed locking (e.g., Redis, ZooKeeper) or database-level transactions

### Security

- No authentication or authorization is implemented
- Production would require OAuth2/JWT authentication and role-based access control

### Monitoring and Metrics

- Basic logging is implemented, but production would need:
  - Centralized logging (ELK stack)
  - Metrics collection (Prometheus/Grafana)
  - Distributed tracing

### Pagination

- The transactions endpoint returns all transactions without pagination
- Production would need pagination for performance with large datasets

## Testing

The application includes comprehensive testing:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=WalletServiceConcurrencyTest
```

Test coverage includes:
- Unit tests for models and repositories
- Service-level tests for transfer logic, idempotency, and concurrent access
- Controller tests with MockMvc for API validation
- Comprehensive validation test cases

## License

[MIT License](LICENSE) 