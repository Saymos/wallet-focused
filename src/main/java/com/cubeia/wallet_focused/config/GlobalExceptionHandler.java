package com.cubeia.wallet_focused.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cubeia.wallet_focused.model.InsufficientFundsException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    @ApiResponse(responseCode = "400", description = "Insufficient funds in source account", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INSUFFICIENT_FUNDS",
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_REQUEST",
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Validation errors", 
                content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Validation failed for request parameters",
                LocalDateTime.now(),
                errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Schema(description = "Standard error response")
    public static class ErrorResponse {
        @Schema(description = "HTTP status code", example = "400")
        private final int status;
        
        @Schema(description = "Error code", example = "INSUFFICIENT_FUNDS")
        private final String code;
        
        @Schema(description = "Error message", example = "Insufficient funds in source account")
        private final String message;
        
        @Schema(description = "Timestamp of when the error occurred", example = "2025-01-15T12:34:56.789")
        private final LocalDateTime timestamp;

        public ErrorResponse(int status, String code, String message, LocalDateTime timestamp) {
            this.status = status;
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    @Schema(description = "Validation error response with field-specific errors")
    public static class ValidationErrorResponse extends ErrorResponse {
        @Schema(description = "Field-specific validation errors", 
                example = "{\"amount\":\"Amount must be positive\",\"transactionId\":\"Transaction ID is required\"}")
        private final Map<String, String> errors;

        public ValidationErrorResponse(int status, String code, String message, LocalDateTime timestamp, Map<String, String> errors) {
            super(status, code, message, timestamp);
            this.errors = errors;
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }
} 