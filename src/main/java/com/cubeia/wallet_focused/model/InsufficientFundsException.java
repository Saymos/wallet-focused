package com.cubeia.wallet_focused.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;

@Schema(description = "Exception thrown when a transfer is attempted with insufficient funds in the source account")
public class InsufficientFundsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }
} 