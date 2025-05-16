package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.cubeia.wallet_focused.model.TransactionEntry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object (DTO) for transaction entry information.
 * Used for API communication to represent a single bookkeeping entry.
 */
@Valid
@Schema(description = "Transaction entry information")
public class TransactionEntryDTO {
    @Schema(description = "Unique identifier for the transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Transaction ID is required")
    private UUID transactionId;
    
    @Schema(description = "Account ID this transaction applies to", example = "123e4567-e89b-12d3-a456-426614174001")
    @NotNull(message = "Account ID is required")
    private UUID accountId;
    
    @Schema(description = "Counterparty account ID in this transaction", example = "123e4567-e89b-12d3-a456-426614174002")
    @NotNull(message = "Counterparty ID is required")
    private UUID counterpartyId;
    
    @Schema(description = "Transaction amount", example = "100.00")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @Schema(description = "Type of transaction (DEBIT or CREDIT)", example = "CREDIT")
    @NotNull(message = "Transaction type is required")
    private String type;
    
    @Schema(description = "Timestamp when transaction occurred", example = "2025-01-15T12:34:56.789Z")
    @NotNull(message = "Timestamp is required")
    private Instant timestamp;

    /**
     * Default constructor for serialization frameworks.
     */
    public TransactionEntryDTO() {
    }

    /**
     * Creates a new TransactionEntryDTO with the specified details.
     *
     * @param transactionId the unique transaction identifier
     * @param accountId the account this transaction applies to
     * @param counterpartyId the counterparty account in this transaction
     * @param amount the transaction amount
     * @param type the transaction type (DEBIT or CREDIT)
     * @param timestamp when the transaction occurred
     */
    public TransactionEntryDTO(UUID transactionId, UUID accountId, UUID counterpartyId, 
                               BigDecimal amount, String type, Instant timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.counterpartyId = counterpartyId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    /**
     * Creates a new TransactionEntryDTO from a TransactionEntry model.
     *
     * @param entry the transaction entry model
     * @return a new DTO representing the given model
     */
    public static TransactionEntryDTO fromModel(TransactionEntry entry) {
        return new TransactionEntryDTO(
            entry.getTransactionId(),
            entry.getAccountId(),
            entry.getCounterpartyId(),
            entry.getAmount(),
            entry.getType().toString(),
            entry.getTimestamp()
        );
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
} 