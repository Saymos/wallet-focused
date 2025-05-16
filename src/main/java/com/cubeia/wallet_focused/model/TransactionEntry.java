package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transaction entry representing a debit or credit to an account")
public class TransactionEntry {
    @Schema(description = "Type of transaction: DEBIT or CREDIT")
    public enum Type { DEBIT, CREDIT }

    @Schema(description = "Unique identifier for the transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID transactionId;
    
    @Schema(description = "Account ID this transaction applies to", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID accountId;
    
    @Schema(description = "Counterparty account ID involved in this transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID counterpartyId;
    
    @Schema(description = "Amount of the transaction", example = "50.00")
    private BigDecimal amount;
    
    @Schema(description = "Type of transaction entry (DEBIT or CREDIT)", example = "DEBIT")
    private Type type;
    
    @Schema(description = "Timestamp when the transaction occurred", example = "2023-01-15T12:34:56Z")
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