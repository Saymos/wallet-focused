package com.cubeia.wallet_focused.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for validation errors.
 * Provides a strongly-typed structure for field validation errors.
 */
public class ValidationErrorDTO {
    private List<FieldError> fieldErrors = new ArrayList<>();
    
    public ValidationErrorDTO() {
    }
    
    /**
     * Adds a field error.
     * 
     * @param field the field name
     * @param message the error message
     */
    public void addFieldError(String field, String message) {
        fieldErrors.add(new FieldError(field, message));
    }
    
    /**
     * Gets the list of field errors.
     * 
     * @return the field errors
     */
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
    
    /**
     * Sets the list of field errors.
     * 
     * @param fieldErrors the field errors
     */
    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    
    /**
     * Represents a single field error in a validation error response.
     */
    public static class FieldError {
        private String field;
        private String message;
        
        public FieldError() {
        }
        
        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public void setField(String field) {
            this.field = field;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
} 