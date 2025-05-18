package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class AccountDTOTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        AccountDTO dto = new AccountDTO();
        
        // Assert
        assertNotNull(dto);
    }
    
    @Test
    public void testParameterizedConstructor() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("500.50");
        
        // Act
        AccountDTO dto = new AccountDTO(accountId, balance);
        
        // Assert
        assertEquals(accountId, dto.getAccountId());
        assertEquals(balance, dto.getBalance());
    }
    
    @Test
    public void testSettersAndGetters() {
        // Arrange
        AccountDTO dto = new AccountDTO();
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("750.25");
        
        // Act
        dto.setAccountId(accountId);
        dto.setBalance(balance);
        
        // Assert
        assertEquals(accountId, dto.getAccountId());
        assertEquals(balance, dto.getBalance());
    }
} 