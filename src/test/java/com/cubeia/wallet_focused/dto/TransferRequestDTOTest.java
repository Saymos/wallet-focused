package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.TransferRequest;

public class TransferRequestDTOTest {

    @Test
    public void testParameterizedConstructorAndFieldAccess() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.50");
        
        // Act
        TransferRequestDTO dto = new TransferRequestDTO(transactionId, sourceAccountId, destinationAccountId, amount);
        
        // Assert
        assertEquals(transactionId, dto.transactionId());
        assertEquals(sourceAccountId, dto.sourceAccountId());
        assertEquals(destinationAccountId, dto.destinationAccountId());
        assertEquals(amount, dto.amount());
    }
    
    @Test
    public void testToModel() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.25");
        
        TransferRequestDTO dto = new TransferRequestDTO(transactionId, sourceAccountId, destinationAccountId, amount);
        
        // Act
        TransferRequest model = dto.toModel();
        
        // Assert
        assertNotNull(model);
        assertEquals(transactionId, model.getTransactionId());
        assertEquals(sourceAccountId, model.getSourceAccountId());
        assertEquals(destinationAccountId, model.getDestinationAccountId());
        assertEquals(amount, model.getAmount());
    }
} 