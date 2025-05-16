package com.cubeia.wallet_focused.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.WalletRepository;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final WalletRepository repository;

    public TransactionServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TransactionEntry> getTransactionsByAccount(UUID accountId) {
        return repository.findTransactionsByAccount(accountId);
    }

    @Override
    public boolean accountExists(UUID accountId) {
        return repository.findAccount(accountId) != null;
    }
} 