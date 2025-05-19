Key Differences Between the Two Wallet Projects
Database Implementation
Wallet: Uses Spring Data JPA with database-backed repositories and proper entity mapping
Wallet-focused: Uses in-memory ConcurrentHashMap with no persistence
Double-Entry Bookkeeping
Wallet: Sophisticated implementation with LedgerEntry entities, dedicated DoubleEntryService, and immutable builder pattern
Wallet-focused: Simplified approach with TransactionEntry objects containing DEBIT/CREDIT type enum
Idempotency Handling
Wallet: Database-persistent reference IDs with transaction isolation levels
Wallet-focused: In-memory ConcurrentHashMap of processed transaction IDs (insufficient for clustering)
Currency Handling
Wallet: Full currency support with validation, Currency enum, and CurrencyMismatchException
Wallet-focused: No currency implementation or validation (single currency assumption)
Testing Coverage
Wallet: Comprehensive test suite with specialized tests for each component
Wallet-focused: Focused testing on critical aspects (basic functionality, idempotency, concurrency)
Concurrency Approach
Wallet: Database-level locking with transaction isolation
Wallet-focused: Per-account ReentrantLock with consistent ordering for deadlock prevention
API Documentation
Both projects have thorough Swagger/OpenAPI documentation, with the wallet project covering more endpoints
Error Handling
Wallet: Rich exception hierarchy with specialized exceptions for different scenarios
Wallet-focused: Simpler error handling but still covers the core requirements
Both projects effectively implement the core requirements, but the original wallet project is more sophisticated and production-ready, while the wallet-focused project makes pragmatic simplifications to meet the 4-hour constraint.