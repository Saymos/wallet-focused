package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AccountDTOTest {
    @Test
    public void testParameterizedConstructorAndFieldAccess() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("500.50");
        // Act
        AccountDTO dto = new AccountDTO(accountId, balance);
        // Assert
        assertEquals(accountId, dto.accountId());
        assertEquals(balance, dto.balance());
    }
} 