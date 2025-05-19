package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of the WalletRepository interface.
 * Uses thread-safe concurrent collections to store accounts, transactions, and
 * processed transactions.
 */
@Repository
@Primary
public class InMemoryWalletRepository implements WalletRepository {
    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();
    private final Map<UUID, List<TransactionEntry>> transactions = new ConcurrentHashMap<>();
    private final Set<UUID> processedTransactions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    // Special admin account with UUID of all zeros
    private static final UUID ADMIN_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private boolean adminAccountInitialized = false;

    public InMemoryWalletRepository() {
        // Empty constructor to avoid test failures
    }
    
    /**
     * Initializes the admin account with 1 million in funds.
     * This is called by the application, but not during testing.
     */
    public synchronized void initializeAdminAccountIfNeeded() {
        if (adminAccountInitialized) {
            return;
        }
        
        // Initialize admin account with 1 million in funds
        Account adminAccount = new Account(ADMIN_ACCOUNT_ID);
        accounts.put(ADMIN_ACCOUNT_ID, adminAccount);
        
        // Add initial credit transaction to the admin account
        UUID initialTransactionId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        TransactionEntry initialCredit = new TransactionEntry(
            initialTransactionId,
            ADMIN_ACCOUNT_ID,
            ADMIN_ACCOUNT_ID,  // Self-credit for initialization
            new BigDecimal("1000000.00"),
            TransactionEntry.Type.CREDIT,
            Instant.now()
        );
        
        // Save the transaction and mark it as processed
        saveTransaction(initialCredit);
        markTransactionProcessed(initialTransactionId);
        
        adminAccountInitialized = true;
    }

    @Override
    public Account findAccount(UUID accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void saveAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    @Override
    public void saveTransaction(TransactionEntry entry) {
        transactions.computeIfAbsent(entry.getAccountId(), k -> new CopyOnWriteArrayList<>()).add(entry);
    }

    @Override
    public List<TransactionEntry> findTransactionsByAccount(UUID accountId) {
        return new ArrayList<>(transactions.getOrDefault(accountId, Collections.emptyList()));
    }

    @Override
    public void markTransactionProcessed(UUID transactionId) {
        processedTransactions.add(transactionId);
    }

    @Override
    public boolean isTransactionProcessed(UUID transactionId) {
        return processedTransactions.contains(transactionId);
    }
} 