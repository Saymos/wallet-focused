package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.InMemoryWalletRepository;
import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.model.WalletRepository;

class WalletServiceImplTest {
    private WalletRepository repository;
    private WalletService service;
    private Account sourceAccount;
    private Account destinationAccount;
    private UUID sourceId;
    private UUID destinationId;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        repository = new InMemoryWalletRepository();
        service = new WalletServiceImpl(repository);
        
        sourceId = UUID.randomUUID();
        destinationId = UUID.randomUUID();
        
        sourceAccount = new Account(sourceId, new BigDecimal("100.00"));
        destinationAccount = new Account(destinationId, new BigDecimal("50.00"));
        
        repository.saveAccount(sourceAccount);
        repository.saveAccount(destinationAccount);
    }
    
    @Test
    void testSuccessfulTransfer() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        service.transfer(request);
        
        // Check balances
        Account updatedSource = repository.findAccount(sourceId);
        Account updatedDestination = repository.findAccount(destinationId);
        
        assertEquals(new BigDecimal("70.00"), updatedSource.getBalance());
        assertEquals(new BigDecimal("80.00"), updatedDestination.getBalance());
        
        // Check transaction entries
        List<TransactionEntry> sourceTxs = repository.findTransactionsByAccount(sourceId);
        List<TransactionEntry> destTxs = repository.findTransactionsByAccount(destinationId);
        
        assertEquals(1, sourceTxs.size());
        assertEquals(1, destTxs.size());
        
        TransactionEntry debit = sourceTxs.get(0);
        TransactionEntry credit = destTxs.get(0);
        
        assertEquals(TransactionEntry.Type.DEBIT, debit.getType());
        assertEquals(TransactionEntry.Type.CREDIT, credit.getType());
        assertEquals(transactionId, debit.getTransactionId());
        assertEquals(transactionId, credit.getTransactionId());
        assertEquals(amount, debit.getAmount());
        assertEquals(amount, credit.getAmount());
    }
    
    @Test
    void testNonExistentDestinationAccount() {
        UUID transactionId = UUID.randomUUID();
        UUID newDestinationId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, newDestinationId, amount);
        service.transfer(request);
        
        // Destination account should be created
        Account newDestination = repository.findAccount(newDestinationId);
        assertNotNull(newDestination);
        assertEquals(amount, newDestination.getBalance());
    }
    
    @Test
    void testInsufficientFunds() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.00"); // More than source has
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        
        assertThrows(InsufficientFundsException.class, () -> service.transfer(request));
        
        // Verify no changes were made
        assertEquals(new BigDecimal("100.00"), repository.findAccount(sourceId).getBalance());
        assertEquals(new BigDecimal("50.00"), repository.findAccount(destinationId).getBalance());
    }
    
    @Test
    void testTransferToSameAccount() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, sourceId, amount);
        
        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }
    
    @Test
    void testNegativeAmount() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("-10.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        
        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }
    
    @Test
    void testSourceAccountNotFound() {
        UUID transactionId = UUID.randomUUID();
        UUID nonExistentSourceId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");
        
        TransferRequest request = new TransferRequest(transactionId, nonExistentSourceId, destinationId, amount);
        
        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }
} 