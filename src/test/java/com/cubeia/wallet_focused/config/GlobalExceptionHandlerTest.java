package com.cubeia.wallet_focused.config;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.cubeia.wallet_focused.config.GlobalExceptionHandler.ErrorResponse;
import com.cubeia.wallet_focused.config.GlobalExceptionHandler.ValidationErrorResponse;
import com.cubeia.wallet_focused.model.InsufficientFundsException;

import jakarta.persistence.EntityNotFoundException;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    public void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    public void handleInsufficientFundsException() {
        // Arrange
        String message = "Not enough funds in account";
        InsufficientFundsException ex = new InsufficientFundsException(message);
        
        // Act
        ResponseEntity<ErrorResponse> response = handler.handleInsufficientFundsException(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("INSUFFICIENT_FUNDS", error.getCode());
        assertEquals(message, error.getMessage());
        assertNotNull(error.getTimestamp());
    }
    
    @Test
    public void handleIllegalArgumentException() {
        // Arrange
        String message = "Invalid argument provided";
        IllegalArgumentException ex = new IllegalArgumentException(message);
        
        // Act
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("INVALID_REQUEST", error.getCode());
        assertEquals(message, error.getMessage());
        assertNotNull(error.getTimestamp());
    }
    
    @Test
    public void handleEntityNotFoundException() {
        // Arrange
        String message = "Account not found: " + UUID.randomUUID();
        EntityNotFoundException ex = new EntityNotFoundException(message);
        
        // Act
        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(ex);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("RESOURCE_NOT_FOUND", error.getCode());
        assertEquals(message, error.getMessage());
        assertNotNull(error.getTimestamp());
    }
    
    @Test
    public void handleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        
        FieldError fieldError1 = new FieldError("transferRequestDTO", "amount", "Amount must be positive");
        FieldError fieldError2 = new FieldError("transferRequestDTO", "transactionId", "Transaction ID is required");
        
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));
        
        // Act
        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationExceptions(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("VALIDATION_ERROR", error.getCode());
        assertEquals("Validation failed for request parameters", error.getMessage());
        assertNotNull(error.getTimestamp());
        
        Map<String, String> errors = error.getErrors();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Amount must be positive", errors.get("amount"));
        assertEquals("Transaction ID is required", errors.get("transactionId"));
    }
    
    @Test
    public void handleGeneralException() {
        // Arrange
        String message = "Unexpected error";
        Exception ex = new Exception(message);
        
        // Act
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
        assertEquals("INTERNAL_SERVER_ERROR", error.getCode());
        assertTrue(error.getMessage().contains("An unexpected error occurred"));
        assertNotNull(error.getTimestamp());
    }
} 