package com.cubeia.wallet_focused.dto;

import java.util.UUID;

/**
 * Data Transfer Object for transfer operation responses.
 * Provides a strongly-typed response structure instead of using Maps.
 */
public record TransferResponseDTO(boolean success, UUID transactionId, String error) {
    /**
     * Creates a success response with the transaction ID.
     * 
     * @param transactionId the transaction ID
     * @return a success response
     */
    public static TransferResponseDTO success(UUID transactionId) {
        return new TransferResponseDTO(true, transactionId, null);
    }
    
    /**
     * Creates an error response with the specified error message.
     * 
     * @param errorMessage the error message
     * @return an error response
     */
    public static TransferResponseDTO error(String errorMessage) {
        return new TransferResponseDTO(false, null, errorMessage);
    }
} 