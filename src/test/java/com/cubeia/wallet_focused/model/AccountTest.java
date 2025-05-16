package com.cubeia.wallet_focused.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    void testAccountCreationAndAccessors() {
        UUID accountId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("100.50");
        Account account = new Account(accountId, initialBalance);
        assertEquals(accountId, account.getAccountId());
        assertEquals(initialBalance, account.getBalance());
    }

    @Test
    void testSetters() {
        Account account = new Account(UUID.randomUUID(), BigDecimal.ZERO);
        BigDecimal newBalance = new BigDecimal("200.00");
        account.setBalance(newBalance);
        assertEquals(newBalance, account.getBalance());
    }
} 