package com.cubeia.wallet_focused.service;

import java.util.List;
import java.util.UUID;

import com.cubeia.wallet_focused.model.TransactionEntry;

public interface TransactionService {
    List<TransactionEntry> getTransactionsByAccount(UUID accountId);
    boolean accountExists(UUID accountId);
} 