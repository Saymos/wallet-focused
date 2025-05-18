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
    public void testParameterizedConstructorAndFieldAccess() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.50");
        TransactionEntryDTO.Type type = TransactionEntryDTO.Type.CREDIT;
        Instant timestamp = Instant.now();
        
        // Act
        TransactionEntryDTO dto = new TransactionEntryDTO(
            transactionId, accountId, counterpartyId, amount, type, timestamp);
        
        // Assert
        assertEquals(transactionId, dto.transactionId());
        assertEquals(accountId, dto.accountId());
        assertEquals(counterpartyId, dto.counterpartyId());
        assertEquals(amount, dto.amount());
        assertEquals(type, dto.type());
        assertEquals(timestamp, dto.timestamp());
    }
    
    @Test
    public void testFromModelCredit() {
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
        assertEquals(transactionId, dto.transactionId());
        assertEquals(accountId, dto.accountId());
        assertEquals(counterpartyId, dto.counterpartyId());
        assertEquals(amount, dto.amount());
        assertEquals(TransactionEntryDTO.Type.CREDIT, dto.type());
        assertEquals(timestamp, dto.timestamp());
    }
    
    @Test
    public void testFromModelDebit() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        TransactionEntry.Type type = TransactionEntry.Type.DEBIT;
        Instant timestamp = Instant.now();
        
        TransactionEntry model = new TransactionEntry(
            transactionId, accountId, counterpartyId, amount, type, timestamp
        );
        
        // Act
        TransactionEntryDTO dto = TransactionEntryDTO.fromModel(model);
        
        // Assert
        assertNotNull(dto);
        assertEquals(transactionId, dto.transactionId());
        assertEquals(accountId, dto.accountId());
        assertEquals(counterpartyId, dto.counterpartyId());
        assertEquals(amount, dto.amount());
        assertEquals(TransactionEntryDTO.Type.DEBIT, dto.type());
        assertEquals(timestamp, dto.timestamp());
    }
} 