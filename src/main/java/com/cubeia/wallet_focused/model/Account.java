package com.cubeia.wallet_focused.model;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Account entity that represents a wallet in the system.
 * This implementation follows the event sourcing pattern - the account balance
 * is not stored directly, but calculated from transaction entries.
 * <p>
 * See the README.md for more information about the event sourcing approach.
 */
@Schema(description = "Account information")
public class Account {
    /**
     * Unique identifier for the account.
     */
    @Schema(description = "Unique identifier for the account", example = "123e4567-e89b-12d3-a456-426614174000")
    private final UUID accountId;
    
    /**
     * Creates a new Account with the specified ID.
     *
     * @param accountId the unique identifier for the account
     */
    public Account(UUID accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the unique identifier of this account.
     *
     * @return the account's UUID
     */
    public UUID getAccountId() {
        return accountId;
    }
} 