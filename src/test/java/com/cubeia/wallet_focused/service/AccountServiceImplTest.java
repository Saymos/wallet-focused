package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.EntityNotFoundException;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.WalletRepository;

public class AccountServiceImplTest {

    @Mock
    private WalletRepository repository;
    
    private AccountServiceImpl accountService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountServiceImpl(repository);
    }
    
    @Test
    public void testGetAccount_WhenAccountExists() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        Account expectedAccount = new Account(accountId);
        
        when(repository.findAccount(accountId)).thenReturn(expectedAccount);
        
        // Act
        Optional<Account> result = accountService.getAccount(accountId);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedAccount, result.get());
    }
    
    @Test
    public void testGetAccount_WhenAccountDoesNotExist() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        
        when(repository.findAccount(accountId)).thenReturn(null);
        
        // Act
        Optional<Account> result = accountService.getAccount(accountId);
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    public void testCalculateBalance_WhenAccountExists() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        Account account = new Account(accountId);
        
        // Create transaction entries for the account
        TransactionEntry entry1 = new TransactionEntry(
            UUID.randomUUID(), accountId, counterpartyId, 
            new BigDecimal("100.00"), TransactionEntry.Type.CREDIT, Instant.now());
            
        TransactionEntry entry2 = new TransactionEntry(
            UUID.randomUUID(), accountId, counterpartyId, 
            new BigDecimal("50.00"), TransactionEntry.Type.DEBIT, Instant.now());
            
        TransactionEntry entry3 = new TransactionEntry(
            UUID.randomUUID(), accountId, counterpartyId, 
            new BigDecimal("25.00"), TransactionEntry.Type.CREDIT, Instant.now());
            
        List<TransactionEntry> entries = Arrays.asList(entry1, entry2, entry3);
        
        when(repository.findAccount(accountId)).thenReturn(account);
        when(repository.findTransactionsByAccount(accountId)).thenReturn(entries);
        
        // Expected balance: 100 (credit) - 50 (debit) + 25 (credit) = 75
        BigDecimal expectedBalance = new BigDecimal("75.00");
        
        // Act
        BigDecimal actualBalance = accountService.calculateBalance(accountId);
        
        // Assert
        assertEquals(0, expectedBalance.compareTo(actualBalance));
    }
    
    @Test
    public void testCalculateBalance_WhenNoTransactions() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId);
        
        when(repository.findAccount(accountId)).thenReturn(account);
        when(repository.findTransactionsByAccount(accountId)).thenReturn(List.of());
        
        // Expected balance: 0 (no transactions)
        BigDecimal expectedBalance = BigDecimal.ZERO;
        
        // Act
        BigDecimal actualBalance = accountService.calculateBalance(accountId);
        
        // Assert
        assertEquals(0, expectedBalance.compareTo(actualBalance));
    }
    
    @Test
    public void testCalculateBalance_WhenAccountDoesNotExist() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        
        when(repository.findAccount(accountId)).thenReturn(null);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> accountService.calculateBalance(accountId));
    }
} 