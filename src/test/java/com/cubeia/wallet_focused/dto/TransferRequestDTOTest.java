package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.TransferRequest;

public class TransferRequestDTOTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        TransferRequestDTO dto = new TransferRequestDTO();
        
        // Assert
        assertNotNull(dto);
    }
    
    @Test
    public void testParameterizedConstructor() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.50");
        
        // Act
        TransferRequestDTO dto = new TransferRequestDTO(transactionId, sourceAccountId, destinationAccountId, amount);
        
        // Assert
        assertEquals(transactionId, dto.getTransactionId());
        assertEquals(sourceAccountId, dto.getSourceAccountId());
        assertEquals(destinationAccountId, dto.getDestinationAccountId());
        assertEquals(amount, dto.getAmount());
    }
    
    @Test
    public void testSettersAndGetters() {
        // Arrange
        TransferRequestDTO dto = new TransferRequestDTO();
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("200.75");
        
        // Act
        dto.setTransactionId(transactionId);
        dto.setSourceAccountId(sourceAccountId);
        dto.setDestinationAccountId(destinationAccountId);
        dto.setAmount(amount);
        
        // Assert
        assertEquals(transactionId, dto.getTransactionId());
        assertEquals(sourceAccountId, dto.getSourceAccountId());
        assertEquals(destinationAccountId, dto.getDestinationAccountId());
        assertEquals(amount, dto.getAmount());
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