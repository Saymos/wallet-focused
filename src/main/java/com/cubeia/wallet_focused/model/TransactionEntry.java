package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionEntry {
    public enum Type { DEBIT, CREDIT }

    private UUID transactionId;
    private UUID accountId;
    private UUID counterpartyId;
    private BigDecimal amount;
    private Type type;
    private Instant timestamp;

    public TransactionEntry(UUID transactionId, UUID accountId, UUID counterpartyId, BigDecimal amount, Type type, Instant timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.counterpartyId = counterpartyId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }

    public TransactionEntry() {}

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getCounterpartyId() {
        return counterpartyId;
    }

    public void setCounterpartyId(UUID counterpartyId) {
        this.counterpartyId = counterpartyId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
} 