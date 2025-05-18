package com.cubeia.wallet_focused.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for validation errors.
 * Provides a strongly-typed structure for field validation errors.
 */
public record ValidationErrorDTO(List<FieldError> fieldErrors) {
    public static record FieldError(String field, String message) {}
    
    /**
     * Creates a new ValidationErrorDTO with an empty list of field errors.
     *
     * @return a new ValidationErrorDTO with no errors
     */
    public static ValidationErrorDTO create() {
        return new ValidationErrorDTO(new ArrayList<>());
    }
    
    /**
     * Creates a new ValidationErrorDTO with the given error added to the current errors.
     *
     * @param field the field name
     * @param message the error message
     * @return a new ValidationErrorDTO with the added error
     */
    public ValidationErrorDTO withError(String field, String message) {
        List<FieldError> newErrors = new ArrayList<>(fieldErrors);
        newErrors.add(new FieldError(field, message));
        return new ValidationErrorDTO(newErrors);
    }
} 