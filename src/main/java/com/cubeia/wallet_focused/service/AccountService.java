package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.cubeia.wallet_focused.model.Account;

/**
 * Service for accessing and managing accounts.
 */
public interface AccountService {
    
    /**
     * Find an account by its ID.
     *
     * @param accountId the unique identifier of the account to find
     * @return an Optional containing the account if found, or empty if not found
     */
    Optional<Account> getAccount(UUID accountId);
    
    /**
     * Calculate the balance of an account from its transaction history.
     * This implements the event sourcing pattern where the balance is derived
     * rather than stored directly.
     * 
     * @param accountId the unique identifier of the account
     * @return the calculated balance, or BigDecimal.ZERO if account has no transactions
     * @throws jakarta.persistence.EntityNotFoundException if the account doesn't exist
     */
    BigDecimal calculateBalance(UUID accountId);
} 