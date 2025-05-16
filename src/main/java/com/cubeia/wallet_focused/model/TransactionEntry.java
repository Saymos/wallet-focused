package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a single transaction entry in the double-entry bookkeeping system.
 * Each transfer generates two entries - a DEBIT from the source account and a CREDIT
 * to the destination account. This is the fundamental building block for the
 * event sourcing pattern implemented in this wallet service.
 */
@Schema(description = "Transaction entry representing a debit or credit to an account")
public class TransactionEntry {
    /**
     * The type of transaction entry.
     * <ul>
     * <li>DEBIT: Decreases the account balance</li>
     * <li>CREDIT: Increases the account balance</li>
     * </ul>
     */
    @Schema(description = "Type of transaction: DEBIT or CREDIT")
    public enum Type { DEBIT, CREDIT }

    /**
     * Unique identifier for the transaction. Used to link together related
     * transaction entries and for idempotency.
     */
    @Schema(description = "Unique identifier for the transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID transactionId;
    
    /**
     * The account ID this transaction applies to.
     */
    @Schema(description = "Account ID this transaction applies to", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID accountId;
    
    /**
     * The counterparty account ID involved in this transaction.
     */
    @Schema(description = "Counterparty account ID involved in this transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID counterpartyId;
    
    /**
     * The amount of the transaction (always positive).
     */
    @Schema(description = "Amount of the transaction", example = "50.00")
    private BigDecimal amount;
    
    /**
     * Whether this entry is a DEBIT or CREDIT.
     */
    @Schema(description = "Type of transaction entry (DEBIT or CREDIT)", example = "DEBIT")
    private Type type;
    
    /**
     * Timestamp when the transaction occurred.
     */
    @Schema(description = "Timestamp when the transaction occurred", example = "2023-01-15T12:34:56Z")
    private Instant timestamp;

    /**
     * Creates a new transaction entry with the specified details.
     *
     * @param transactionId unique identifier for the transaction
     * @param accountId the account this entry applies to
     * @param counterpartyId the counterparty account in this transaction
     * @param amount the transaction amount (always positive)
     * @param type the entry type (DEBIT or CREDIT)
     * @param timestamp when the transaction occurred
     */
    public TransactionEntry(UUID transactionId, UUID accountId, UUID counterpartyId, BigDecimal amount, Type type, Instant timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.counterpartyId = counterpartyId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }

    /**
     * Default constructor for serialization frameworks.
     */
    public TransactionEntry() {}

    /**
     * Gets the unique transaction identifier.
     *
     * @return the transaction ID
     */
    public UUID getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the unique transaction identifier.
     *
     * @param transactionId the transaction ID to set
     */
    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the account ID this transaction applies to.
     *
     * @return the account ID
     */
    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Sets the account ID this transaction applies to.
     *
     * @param accountId the account ID to set
     */
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the counterparty account ID in this transaction.
     *
     * @return the counterparty account ID
     */
    public UUID getCounterpartyId() {
        return counterpartyId;
    }

    /**
     * Sets the counterparty account ID in this transaction.
     *
     * @param counterpartyId the counterparty account ID to set
     */
    public void setCounterpartyId(UUID counterpartyId) {
        this.counterpartyId = counterpartyId;
    }

    /**
     * Gets the transaction amount.
     *
     * @return the transaction amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the transaction amount.
     *
     * @param amount the transaction amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the type of this transaction entry (DEBIT or CREDIT).
     *
     * @return the transaction type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of this transaction entry.
     *
     * @param type the transaction type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Gets the timestamp when this transaction occurred.
     *
     * @return the transaction timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when this transaction occurred.
     *
     * @param timestamp the transaction timestamp to set
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
} 