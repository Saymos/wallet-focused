package com.cubeia.wallet_focused.model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AccountTest {
    @Test
    void testAccountCreationAndAccessors() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId);
        assertEquals(accountId, account.getAccountId());
    }
} 