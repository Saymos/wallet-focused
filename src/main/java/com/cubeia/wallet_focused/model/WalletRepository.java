package com.cubeia.wallet_focused.model;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for wallet operations.
 * Provides methods to retrieve and store accounts, transactions,
 * and track processed transactions for idempotency.
 */
public interface WalletRepository {
    
    /**
     * Finds an account by its ID.
     *
     * @param accountId the ID of the account to find
     * @return the account if found, or null if not found
     */
    Account findAccount(UUID accountId);
    
    /**
     * Saves an account to the repository.
     *
     * @param account the account to save
     */
    void saveAccount(Account account);
    
    /**
     * Saves a transaction entry to the repository.
     *
     * @param entry the transaction entry to save
     */
    void saveTransaction(TransactionEntry entry);
    
    /**
     * Finds all transaction entries for an account.
     *
     * @param accountId the ID of the account to find transactions for
     * @return a list of transaction entries for the account, may be empty but never null
     */
    List<TransactionEntry> findTransactionsByAccount(UUID accountId);
    
    /**
     * Marks a transaction as processed for idempotency.
     *
     * @param transactionId the ID of the transaction to mark as processed
     */
    void markTransactionProcessed(UUID transactionId);
    
    /**
     * Checks if a transaction has already been processed.
     *
     * @param transactionId the ID of the transaction to check
     * @return true if the transaction has been processed, false otherwise
     */
    boolean isTransactionProcessed(UUID transactionId);
} 