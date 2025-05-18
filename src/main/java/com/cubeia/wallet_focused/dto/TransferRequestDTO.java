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
public record TransferRequestDTO(
    @NotNull(message = "Transaction ID is required") 
    @Schema(description = "Unique transaction identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID transactionId,
    
    @NotNull(message = "Source account ID is required")
    @Schema(description = "Source account identifier", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID sourceAccountId,
    
    @NotNull(message = "Destination account ID is required")
    @Schema(description = "Destination account identifier", example = "123e4567-e89b-12d3-a456-426614174002")
    UUID destinationAccountId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Transfer amount", example = "100.00")
    BigDecimal amount
) {
    
    /**
     * Converts this DTO to a domain model TransferRequest.
     *
     * @return a new TransferRequest with the same values
     */
    public TransferRequest toModel() {
        return new TransferRequest(transactionId, sourceAccountId, destinationAccountId, amount);
    }
    
    public static TransferRequestDTO fromModel(TransferRequest model) {
        return new TransferRequestDTO(
            model.getTransactionId(),
            model.getSourceAccountId(),
            model.getDestinationAccountId(),
            model.getAmount()
        );
    }
} 