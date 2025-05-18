package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.WalletRepository;

public class TransactionServiceImplTest {

    @Mock
    private WalletRepository repository;
    
    private TransactionServiceImpl transactionService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionServiceImpl(repository);
    }
    
    @Test
    public void testGetTransactionsByAccount() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID transactionId1 = UUID.randomUUID();
        UUID transactionId2 = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        
        TransactionEntry entry1 = new TransactionEntry(
            transactionId1, accountId, counterpartyId, 
            new BigDecimal("100.00"), TransactionEntry.Type.CREDIT, Instant.now());
            
        TransactionEntry entry2 = new TransactionEntry(
            transactionId2, accountId, counterpartyId, 
            new BigDecimal("50.00"), TransactionEntry.Type.DEBIT, Instant.now());
            
        List<TransactionEntry> expectedEntries = Arrays.asList(entry1, entry2);
        
        when(repository.findTransactionsByAccount(accountId)).thenReturn(expectedEntries);
        
        // Act
        List<TransactionEntry> actualEntries = transactionService.getTransactionsByAccount(accountId);
        
        // Assert
        assertEquals(expectedEntries.size(), actualEntries.size());
        assertEquals(expectedEntries, actualEntries);
    }
    
    @Test
    public void testAccountExists_WhenAccountExists() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId);
        
        when(repository.findAccount(accountId)).thenReturn(account);
        
        // Act
        boolean exists = transactionService.accountExists(accountId);
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    public void testAccountExists_WhenAccountDoesNotExist() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        
        when(repository.findAccount(accountId)).thenReturn(null);
        
        // Act
        boolean exists = transactionService.accountExists(accountId);
        
        // Assert
        assertFalse(exists);
    }
} 