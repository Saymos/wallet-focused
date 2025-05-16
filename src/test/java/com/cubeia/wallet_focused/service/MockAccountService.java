package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.WalletRepository;

/**
 * Mock implementation of AccountService for testing.
 * This simplifies test setup by calculating balance directly from the repository.
 */
public class MockAccountService implements AccountService {
    
    private final WalletRepository repository;
    private final Map<UUID, BigDecimal> mockedBalances = new ConcurrentHashMap<>();
    
    public MockAccountService(WalletRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Set a mocked balance value for an account to override calculation.
     * Useful for tests that need to preset balances.
     *
     * @param accountId the account ID
     * @param balance the balance to return for the account
     */
    public void setMockedBalance(UUID accountId, BigDecimal balance) {
        mockedBalances.put(accountId, balance);
    }
    
    @Override
    public Optional<Account> getAccount(UUID accountId) {
        Account account = repository.findAccount(accountId);
        return Optional.ofNullable(account);
    }
    
    @Override
    public BigDecimal calculateBalance(UUID accountId) {
        // If a mocked balance is set, return it
        if (mockedBalances.containsKey(accountId)) {
            return mockedBalances.get(accountId);
        }
        
        // Otherwise calculate from transaction entries
        return repository.findTransactionsByAccount(accountId).stream()
            .map(entry -> entry.getType() == TransactionEntry.Type.CREDIT 
                ? entry.getAmount() 
                : entry.getAmount().negate())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 