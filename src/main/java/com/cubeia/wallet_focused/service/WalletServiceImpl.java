package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.time.Instant;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.model.WalletRepository;

public class WalletServiceImpl implements WalletService {
    private final WalletRepository repository;

    public WalletServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public void transfer(TransferRequest request) {
        // Idempotency check
        if (repository.isTransactionProcessed(request.getTransactionId())) {
            return;
        }
        // Validate inputs
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }
        
        // Find source account
        Account sourceAccount = repository.findAccount(request.getSourceAccountId());
        if (sourceAccount == null) {
            throw new IllegalArgumentException("Source account not found");
        }
        
        // Check sufficient funds
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in source account");
        }
        
        // Find or create destination account
        Account destinationAccount = repository.findAccount(request.getDestinationAccountId());
        if (destinationAccount == null) {
            destinationAccount = new Account(request.getDestinationAccountId(), BigDecimal.ZERO);
            repository.saveAccount(destinationAccount);
        }
        
        // Create transaction entries
        Instant timestamp = Instant.now();
        
        TransactionEntry debitEntry = new TransactionEntry(
            request.getTransactionId(),
            sourceAccount.getAccountId(),
            destinationAccount.getAccountId(),
            request.getAmount(),
            TransactionEntry.Type.DEBIT,
            timestamp
        );
        
        TransactionEntry creditEntry = new TransactionEntry(
            request.getTransactionId(),
            destinationAccount.getAccountId(),
            sourceAccount.getAccountId(),
            request.getAmount(),
            TransactionEntry.Type.CREDIT,
            timestamp
        );
        
        // Update balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
        
        // Save everything
        repository.saveAccount(sourceAccount);
        repository.saveAccount(destinationAccount);
        repository.saveTransaction(debitEntry);
        repository.saveTransaction(creditEntry);
        // Mark as processed
        repository.markTransactionProcessed(request.getTransactionId());
    }
} 