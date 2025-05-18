package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.WalletRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Implementation of the AccountService interface.
 * Provides account-related operations including retrieving accounts
 * and calculating balances using event sourcing.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    
    private final WalletRepository repository;

    /**
     * Creates a new AccountServiceImpl with the specified repository.
     *
     * @param repository the wallet repository to use
     */
    public AccountServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccount(UUID accountId) {
        logger.debug("Fetching account: accountId={}", accountId);
        Account account = repository.findAccount(accountId);
        
        if (account == null) {
            logger.info("Account not found: accountId={}", accountId);
            return Optional.empty();
        }
        
        logger.debug("Account found: accountId={}", accountId);
        return Optional.of(account);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateBalance(UUID accountId) {
        logger.debug("Calculating balance for account: accountId={}", accountId);
        
        // Check if account exists
        Account account = repository.findAccount(accountId);
        if (account == null) {
            logger.warn("Cannot calculate balance - account not found: accountId={}", accountId);
            throw new EntityNotFoundException("Account not found: " + accountId);
        }
        
        // Get all transactions for the account
        List<TransactionEntry> entries = repository.findTransactionsByAccount(accountId);
        
        // Calculate balance using transaction entries
        BigDecimal balance = entries.stream()
            .map(entry -> entry.getType() == TransactionEntry.Type.CREDIT
                ? entry.getAmount()
                : entry.getAmount().negate())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        logger.debug("Calculated balance for account: accountId={}, balance={}", accountId, balance);
        return balance;
    }
} 