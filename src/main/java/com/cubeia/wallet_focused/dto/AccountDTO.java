package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for account information.
 * This is used for API communication with clients.
 */
@Valid
@Schema(description = "Account information")
public class AccountDTO {
    @Schema(description = "Unique identifier for the account", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Account ID is required")
    private UUID accountId;
    
    @Schema(description = "Current balance of the account", example = "1000.00")
    @NotNull(message = "Balance is required")
    private BigDecimal balance;

    /**
     * Default constructor for serialization frameworks.
     */
    public AccountDTO() {
    }

    /**
     * Creates a new AccountDTO with the specified ID and balance.
     *
     * @param accountId the unique identifier for the account
     * @param balance the current balance of the account
     */
    public AccountDTO(UUID accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    /**
     * Gets the unique identifier of this account.
     *
     * @return the account's UUID
     */
    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Sets the unique identifier of this account.
     *
     * @param accountId the UUID to set
     */
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the current balance of this account.
     *
     * @return the account's balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Sets the current balance of this account.
     *
     * @param balance the balance to set
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
} 