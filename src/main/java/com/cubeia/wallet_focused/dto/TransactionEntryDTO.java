package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.cubeia.wallet_focused.model.TransactionEntry;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) for TransactionEntry.
 * Used to provide a clean separation between the API layer and the domain model.
 */
@Schema(description = "Transaction entry representing a debit or credit to an account")
public class TransactionEntryDTO {
    
    /**
     * The type of transaction entry.
     */
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

    /**
     * Default constructor for serialization frameworks.
     */
    public TransactionEntryDTO() {
        // Default constructor for serialization frameworks
    }

    /**
     * Creates a new transaction entry DTO with the specified details.
     */
    public TransactionEntryDTO(UUID transactionId, UUID accountId, UUID counterpartyId, 
                             BigDecimal amount, Type type, Instant timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.counterpartyId = counterpartyId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    /**
     * Factory method to create a DTO from a model object.
     * 
     * @param model the TransactionEntry model to convert
     * @return a new TransactionEntryDTO
     */
    public static TransactionEntryDTO fromModel(TransactionEntry model) {
        Type dtoType = model.getType() == TransactionEntry.Type.DEBIT ? 
                Type.DEBIT : Type.CREDIT;
                
        return new TransactionEntryDTO(
            model.getTransactionId(),
            model.getAccountId(),
            model.getCounterpartyId(),
            model.getAmount(),
            dtoType,
            model.getTimestamp()
        );
    }

    // Getters and setters
    
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