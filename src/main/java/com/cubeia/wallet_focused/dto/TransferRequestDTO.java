package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.cubeia.wallet_focused.model.TransferRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object (DTO) for transfer request information.
 * Used for API communication to represent a transfer request from the client.
 */
@Valid
@Schema(description = "Transfer request information")
public class TransferRequestDTO {
    
    @Schema(description = "Unique transaction ID for idempotency", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    @NotNull(message = "Transaction ID is required")
    private UUID transactionId;
    
    @Schema(description = "Source account ID for the transfer", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    @NotNull(message = "Source account ID is required")
    private UUID sourceAccountId;
    
    @Schema(description = "Destination account ID for the transfer", example = "123e4567-e89b-12d3-a456-426614174002", required = true)
    @NotNull(message = "Destination account ID is required")
    private UUID destinationAccountId;
    
    @Schema(description = "Amount to transfer", example = "100.00", required = true)
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    /**
     * Default constructor for serialization frameworks.
     */
    public TransferRequestDTO() {
    }
    
    /**
     * Creates a new TransferRequestDTO with the specified details.
     *
     * @param transactionId unique ID for idempotency
     * @param sourceAccountId source account for the transfer
     * @param destinationAccountId destination account for the transfer
     * @param amount amount to transfer
     */
    public TransferRequestDTO(UUID transactionId, UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }
    
    /**
     * Converts this DTO to a domain model TransferRequest.
     *
     * @return a new TransferRequest with the same values
     */
    public TransferRequest toModel() {
        return new TransferRequest(
            this.transactionId,
            this.sourceAccountId,
            this.destinationAccountId,
            this.amount
        );
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