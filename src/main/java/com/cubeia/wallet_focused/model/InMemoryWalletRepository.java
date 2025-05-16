package com.cubeia.wallet_focused.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory implementation of the WalletRepository interface.
 * Uses thread-safe concurrent collections to store accounts, transactions, and
 * processed transactions.
 */
public class InMemoryWalletRepository implements WalletRepository {
    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();
    private final Map<UUID, List<TransactionEntry>> transactions = new ConcurrentHashMap<>();
    private final Set<UUID> processedTransactions = Collections.newSetFromMap(new ConcurrentHashMap<>());

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