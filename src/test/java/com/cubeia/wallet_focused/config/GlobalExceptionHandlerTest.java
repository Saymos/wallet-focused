package com.cubeia.wallet_focused.config;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.cubeia.wallet_focused.dto.ValidationErrorDTO;
import com.cubeia.wallet_focused.model.InsufficientFundsException;

import jakarta.persistence.EntityNotFoundException;

class GlobalExceptionHandlerTest {

    @Test
    void testHandleEntityNotFoundException() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String errorMessage = "Account not found";
        EntityNotFoundException ex = new EntityNotFoundException(errorMessage);
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleEntityNotFound(ex);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    }
    
    @Test
    void testHandleInsufficientFundsException() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String errorMessage = "Insufficient funds in account";
        InsufficientFundsException ex = new InsufficientFundsException(errorMessage);
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleInsufficientFunds(ex);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
    }
    
    @Test
    void testHandleIllegalArgumentException() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String errorMessage = "Amount must be positive";
        IllegalArgumentException ex = new IllegalArgumentException(errorMessage);
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalArgument(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }
    
    @Test
    void testHandleValidationExceptions() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        
        // Mock the MethodArgumentNotValidException and its components
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("transferRequest", "amount", "Amount must be positive");
        FieldError fieldError2 = new FieldError("transferRequest", "sourceAccountId", "Source account ID is required");
        
        Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        
        // Act
        ResponseEntity<ValidationErrorDTO> response = handler.handleValidationExceptions(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Check field errors
        List<ValidationErrorDTO.FieldError> fieldErrors = response.getBody().fieldErrors();
        assertEquals(2, fieldErrors.size());
        
        boolean foundAmountError = false;
        boolean foundSourceAccountError = false;
        
        for (ValidationErrorDTO.FieldError fieldError : fieldErrors) {
            if (fieldError.field().equals("amount") && fieldError.message().equals("Amount must be positive")) {
                foundAmountError = true;
            } else if (fieldError.field().equals("sourceAccountId") && fieldError.message().equals("Source account ID is required")) {
                foundSourceAccountError = true;
            }
        }
        
        assertTrue(foundAmountError, "Should contain amount error");
        assertTrue(foundSourceAccountError, "Should contain sourceAccountId error");
    }
    
    @Test
    void testHandleUnexpectedException() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new RuntimeException("Unexpected error");
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleException(ex);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
    }
} 