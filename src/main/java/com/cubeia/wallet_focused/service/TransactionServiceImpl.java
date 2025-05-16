package com.cubeia.wallet_focused.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.WalletRepository;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    
    private final WalletRepository repository;

    public TransactionServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TransactionEntry> getTransactionsByAccount(UUID accountId) {
        logger.debug("Fetching transactions for account: accountId={}", accountId);
        List<TransactionEntry> transactions = repository.findTransactionsByAccount(accountId);
        logger.debug("Found {} transactions for account: accountId={}", transactions.size(), accountId);
        return transactions;
    }

    @Override
    public boolean accountExists(UUID accountId) {
        logger.debug("Checking if account exists: accountId={}", accountId);
        Account account = repository.findAccount(accountId);
        boolean exists = account != null;
        logger.debug("Account {} exists: {}", accountId, exists);
        return exists;
    }
} 