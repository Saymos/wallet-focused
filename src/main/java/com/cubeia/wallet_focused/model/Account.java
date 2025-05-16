package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    private UUID accountId;
    private BigDecimal balance;

    public Account(UUID accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public Account() {}

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