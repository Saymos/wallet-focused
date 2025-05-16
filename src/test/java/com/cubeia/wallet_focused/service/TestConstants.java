package com.cubeia.wallet_focused.service;

import java.util.UUID;

/**
 * Constants for use in test classes.
 */
public class TestConstants {
    
    /**
     * System account ID used for initializing balances in tests.
     * This account is considered to have unlimited funds.
     */
    public static final UUID SYSTEM_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    
    private TestConstants() {
        // Prevent instantiation
    }
} 