package com.cubeia.wallet_focused.model;

import java.util.List;
import java.util.UUID;

public interface WalletRepository {
    Account findAccount(UUID accountId);
    void saveAccount(Account account);
    void saveTransaction(TransactionEntry entry);
    List<TransactionEntry> findTransactionsByAccount(UUID accountId);
    void markTransactionProcessed(UUID transactionId);
    boolean isTransactionProcessed(UUID transactionId);
} 