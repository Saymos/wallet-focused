package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.service.WalletService;
import com.cubeia.wallet_focused.service.WalletServiceImpl;

class DoubleEntryTransferTest {
    private WalletRepository repo;
    private WalletService walletService;
    private Account accountA;
    private Account accountB;

    @BeforeEach
    void setUp() {
        repo = new InMemoryWalletRepository();
        walletService = new WalletServiceImpl(repo);
        accountA = new Account(UUID.randomUUID(), new BigDecimal("100.00"));
        accountB = new Account(UUID.randomUUID(), new BigDecimal("50.00"));
        repo.saveAccount(accountA);
        repo.saveAccount(accountB);
    }

    @Test
    void testDoubleEntryTransfer() {
        UUID txId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30.00");
        // Simulate transfer: debit A, credit B
        TransactionEntry debit = new TransactionEntry(txId, accountA.getAccountId(), accountB.getAccountId(), amount, TransactionEntry.Type.DEBIT, Instant.now());
        TransactionEntry credit = new TransactionEntry(txId, accountB.getAccountId(), accountA.getAccountId(), amount, TransactionEntry.Type.CREDIT, Instant.now());
        accountA.setBalance(accountA.getBalance().subtract(amount));
        accountB.setBalance(accountB.getBalance().add(amount));
        repo.saveAccount(accountA);
        repo.saveAccount(accountB);
        repo.saveTransaction(debit);
        repo.saveTransaction(credit);
        // Assert balances
        assertEquals(new BigDecimal("70.00"), repo.findAccount(accountA.getAccountId()).getBalance());
        assertEquals(new BigDecimal("80.00"), repo.findAccount(accountB.getAccountId()).getBalance());
        // Assert ledger entries
        List<TransactionEntry> aTxs = repo.findTransactionsByAccount(accountA.getAccountId());
        List<TransactionEntry> bTxs = repo.findTransactionsByAccount(accountB.getAccountId());
        assertTrue(aTxs.stream().anyMatch(e -> e.getType() == TransactionEntry.Type.DEBIT && e.getTransactionId().equals(txId)));
        assertTrue(bTxs.stream().anyMatch(e -> e.getType() == TransactionEntry.Type.CREDIT && e.getTransactionId().equals(txId)));
    }

    @Test
    void testTransferSameAccount() {
        // Create a transfer request with same source and destination account
        TransferRequest request = new TransferRequest(
            UUID.randomUUID(),
            accountA.getAccountId(),
            accountA.getAccountId(), // Same account as source
            new BigDecimal("10.00")
        );
        
        // Assert that trying to transfer to the same account throws an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.transfer(request);
        });
        
        // Verify exception message matches the implementation
        assertEquals("Cannot transfer to same account", exception.getMessage());
        
        // Verify account balance remains unchanged
        assertEquals(new BigDecimal("100.00"), repo.findAccount(accountA.getAccountId()).getBalance());
    }

    @Test
    void testTransferNonPositiveAmount() {
        // Create a transfer request with zero amount
        TransferRequest zeroAmountRequest = new TransferRequest(
            UUID.randomUUID(),
            accountA.getAccountId(),
            accountB.getAccountId(),
            BigDecimal.ZERO // Zero amount
        );
        
        // Assert that trying to transfer zero amount throws an exception
        IllegalArgumentException zeroException = assertThrows(IllegalArgumentException.class, () -> {
            walletService.transfer(zeroAmountRequest);
        });
        
        // Verify exception message
        assertEquals("Amount must be positive", zeroException.getMessage());
        
        // Create a transfer request with negative amount
        TransferRequest negativeAmountRequest = new TransferRequest(
            UUID.randomUUID(),
            accountA.getAccountId(),
            accountB.getAccountId(),
            new BigDecimal("-10.00") // Negative amount
        );
        
        // Assert that trying to transfer negative amount throws an exception
        IllegalArgumentException negativeException = assertThrows(IllegalArgumentException.class, () -> {
            walletService.transfer(negativeAmountRequest);
        });
        
        // Verify exception message
        assertEquals("Amount must be positive", negativeException.getMessage());
        
        // Verify account balances remain unchanged
        assertEquals(new BigDecimal("100.00"), repo.findAccount(accountA.getAccountId()).getBalance());
        assertEquals(new BigDecimal("50.00"), repo.findAccount(accountB.getAccountId()).getBalance());
    }
} 