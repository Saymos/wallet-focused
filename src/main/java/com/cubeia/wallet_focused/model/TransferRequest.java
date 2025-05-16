package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request object for fund transfer between accounts")
public class TransferRequest {
    @NotNull(message = "Transaction ID is required")
    @Schema(description = "Unique identifier for the transaction (idempotency key)", 
            example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID transactionId;
    
    @NotNull(message = "Source account ID is required")
    @Schema(description = "Source account ID to debit funds from", 
            example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID sourceAccountId;
    
    @NotNull(message = "Destination account ID is required")
    @Schema(description = "Destination account ID to credit funds to", 
            example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID destinationAccountId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount to transfer (must be positive)", 
            example = "100.00", required = true, minimum = "0.01")
    private BigDecimal amount;

    public TransferRequest() {
    }

    public TransferRequest(UUID transactionId, UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
} 