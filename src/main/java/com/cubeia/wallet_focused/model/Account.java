package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Account information")
public class Account {
    @Schema(description = "Unique identifier for the account", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID accountId;
    
    @Schema(description = "Current balance of the account", example = "1000.00")
    private BigDecimal balance;

    public Account() {}

    public Account(UUID accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
} 