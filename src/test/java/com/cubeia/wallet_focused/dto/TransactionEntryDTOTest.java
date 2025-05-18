package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.TransactionEntry;

public class TransactionEntryDTOTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        TransactionEntryDTO dto = new TransactionEntryDTO();
        
        // Assert
        assertNotNull(dto);
    }
    
    @Test
    public void testParameterizedConstructor() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.50");
        String type = "CREDIT";
        Instant timestamp = Instant.now();
        
        // Act
        TransactionEntryDTO dto = new TransactionEntryDTO(
            transactionId, accountId, counterpartyId, amount, type, timestamp);
        
        // Assert
        assertEquals(transactionId, dto.getTransactionId());
        assertEquals(accountId, dto.getAccountId());
        assertEquals(counterpartyId, dto.getCounterpartyId());
        assertEquals(amount, dto.getAmount());
        assertEquals(type, dto.getType());
        assertEquals(timestamp, dto.getTimestamp());
    }
    
    @Test
    public void testSettersAndGetters() {
        // Arrange
        TransactionEntryDTO dto = new TransactionEntryDTO();
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("200.75");
        String type = "DEBIT";
        Instant timestamp = Instant.now();
        
        // Act
        dto.setTransactionId(transactionId);
        dto.setAccountId(accountId);
        dto.setCounterpartyId(counterpartyId);
        dto.setAmount(amount);
        dto.setType(type);
        dto.setTimestamp(timestamp);
        
        // Assert
        assertEquals(transactionId, dto.getTransactionId());
        assertEquals(accountId, dto.getAccountId());
        assertEquals(counterpartyId, dto.getCounterpartyId());
        assertEquals(amount, dto.getAmount());
        assertEquals(type, dto.getType());
        assertEquals(timestamp, dto.getTimestamp());
    }
    
    @Test
    public void testFromModel() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.25");
        TransactionEntry.Type type = TransactionEntry.Type.CREDIT;
        Instant timestamp = Instant.now();
        
        TransactionEntry model = new TransactionEntry(
            transactionId, accountId, counterpartyId, amount, type, timestamp);
        
        // Act
        TransactionEntryDTO dto = TransactionEntryDTO.fromModel(model);
        
        // Assert
        assertNotNull(dto);
        assertEquals(transactionId, dto.getTransactionId());
        assertEquals(accountId, dto.getAccountId());
        assertEquals(counterpartyId, dto.getCounterpartyId());
        assertEquals(amount, dto.getAmount());
        assertEquals(type.toString(), dto.getType());
        assertEquals(timestamp, dto.getTimestamp());
    }
} 