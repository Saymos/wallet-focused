# Wallet Service API

A Java Spring Boot wallet service that implements basic bookkeeping (accounting) functionality for tracking funds, similar to a bank account. The service is built with a focus on correctness, thread safety, and clear API design as specified in the PRD.

## Features

- **Double-entry bookkeeping** for all transactions, ensuring financial consistency
- **Thread-safe concurrent operations** with deadlock prevention through consistent lock ordering
- **Idempotent transfers** using transaction IDs to prevent duplicate processing
- **Implicit account creation** when an account receives its first transfer
- **Comprehensive validation and error handling** with appropriate HTTP status codes
- **API documentation** via Swagger UI
- **Virtual threads support** (Java 21) for improved concurrency and performance

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

The API consists of three main endpoints as specified in the PRD:

1. **Get Account Balance**: `GET /api/v1/accounts/{id}/balance`
2. **Transfer Funds**: `POST /api/v1/accounts/transfer`
3. **List Transactions**: `GET /api/v1/accounts/{id}/transactions`

### Example API Requests

#### Get Balance

```bash
curl -X GET http://localhost:8080/api/v1/accounts/{accountId}/balance
```

Response:
```json
{
  "accountId": "123e4567-e89b-12d3-a456-426614174001",
  "balance": 100.00
}
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

Response:
```json
{
  "success": true,
  "transactionId": "123e4567-e89b-12d3-a456-426614174000"
}
```

#### List Transactions

```bash
curl -X GET http://localhost:8080/api/v1/accounts/{accountId}/transactions
```

Response:
```json
[
  {
    "transactionId": "123e4567-e89b-12d3-a456-426614174000",
    "accountId": "123e4567-e89b-12d3-a456-426614174001",
    "counterpartyId": "123e4567-e89b-12d3-a456-426614174002",
    "amount": 100.00,
    "type": "DEBIT",
    "timestamp": "2025-05-18T16:22:10.914Z"
  },
  {
    "transactionId": "456e7890-e89b-12d3-a456-426614174001",
    "accountId": "123e4567-e89b-12d3-a456-426614174001",
    "counterpartyId": "789e0123-e89b-12d3-a456-426614174003",
    "amount": 50.00,
    "type": "CREDIT",
    "timestamp": "2025-05-18T16:24:15.782Z"
  }
]
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

### Thread Safety & Concurrency

The wallet service implements robust concurrency control mechanisms to ensure thread safety:

1. **Account-Level Locking**
   - Each account has its own `ReentrantLock` to prevent concurrent modifications
   - Locks are managed in a thread-safe `ConcurrentHashMap`

2. **Deadlock Prevention**
   - Transfer operations that involve two accounts acquire locks in a consistent order
   - Accounts are locked based on UUID comparison (lexicographical order)
   - This prevents circular wait conditions that could lead to deadlocks

3. **Thread-Safe Collections**
   - All data structures are thread-safe:
     - `ConcurrentHashMap` for accounts and transaction storage
     - `CopyOnWriteArrayList` for transaction entries
     - Concurrent set for processed transaction IDs

4. **Virtual Threads**
   - Java 21 virtual threads are used for request handling
   - This provides improved scalability for I/O-bound operations
   - Configured via `spring.threads.virtual.enabled=true`

### Idempotency

The service guarantees idempotency for transfers using transaction IDs:

1. **Unique Transaction IDs**
   - Each transfer requires a client-provided UUID as the transaction ID
   - The system tracks processed transaction IDs in a thread-safe set

2. **Duplicate Detection**
   - Before processing a transfer, the system checks if the transaction ID is already processed
   - Duplicate requests return success without re-applying the transfer
   - This ensures the same operation is never applied twice, even if the client retries

3. **Implementation Details**
   - Uses `Collections.newSetFromMap(new ConcurrentHashMap<>())` for thread-safe storage
   - Transaction IDs are stored indefinitely (in this implementation)
   - In production, a time-based expiration strategy would be implemented

### Validation and Error Handling

The service implements comprehensive validation and structured error handling:

1. **Input Validation**
   - Bean Validation annotations on DTOs (e.g., `@NotNull`, `@Positive`)
   - Custom validation logic in service layer (e.g., no self-transfers, sufficient funds)

2. **HTTP Status Codes**
   - 200 OK: Successful operation
   - 400 Bad Request: Invalid input (negative amount, same source/destination, invalid UUID)
   - 404 Not Found: Account not found
   - 409 Conflict: Insufficient funds
   - 500 Internal Server Error: Unexpected errors

3. **Structured Error Responses**
   - Consistent JSON format for all errors
   - Includes error message, status code, and timestamp
   - For validation errors, includes details about each field error

### Account Creation

- Accounts are created implicitly when they are the destination of a transfer
- No explicit "create account" endpoint is provided, as per the PRD's optional nature of this feature

## Implementation Shortcuts & Production Considerations

This implementation includes several shortcuts that would need to be addressed in a production environment:

### In-Memory Storage

- The current implementation uses in-memory storage (`ConcurrentHashMap`)
- For production, a persistent database with ACID transactions would be required
- The `WalletRepository` interface is designed to facilitate future database integration

### Cluster Scalability

- The current locking mechanism works for a single instance but not for multiple instances
- A production solution would require:
  - Distributed locking (e.g., Redis, ZooKeeper)
  - Database-level transactions and constraints
  - Optimistic concurrency control

### Security

- No authentication or authorization is implemented
- Production would require:
  - OAuth2/JWT authentication
  - Role-based access control
  - API rate limiting
  - Request signing for sensitive operations

### Monitoring and Metrics

- Basic logging is implemented, but production would need:
  - Centralized logging (ELK stack)
  - Metrics collection (Prometheus/Grafana)
  - Distributed tracing (Zipkin/Jaeger)
  - Alerting and monitoring

### Pagination

- The transactions endpoint returns all transactions without pagination
- Production would need:
  - Offset/limit or cursor-based pagination
  - Sorting options
  - Filtering capabilities

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
- Concurrency tests to verify thread safety and deadlock prevention
- Validation tests for error handling

## License

[MIT License](LICENSE) 