package com.cubeia.wallet_focused.model;

/**
 * Custom exception thrown when an entity cannot be found.
 * This is used to replace the JPA EntityNotFoundException.
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityName, String id) {
        super(entityName + " with ID " + id + " not found");
    }
} 