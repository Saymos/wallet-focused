package com.cubeia.wallet_focused.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Data Transfer Object for transfer operation responses.
 * Provides a strongly-typed response structure instead of using Maps.
 */
@JsonInclude(Include.NON_NULL)
public class TransferResponseDTO {
    private boolean success;
    private UUID transactionId;
    private String error;
    
    // Private constructor for factory methods
    private TransferResponseDTO(boolean success, UUID transactionId, String error) {
        this.success = success;
        this.transactionId = transactionId;
        this.error = error;
    }
    
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
    
    // Getters and setters
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public UUID getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
} 