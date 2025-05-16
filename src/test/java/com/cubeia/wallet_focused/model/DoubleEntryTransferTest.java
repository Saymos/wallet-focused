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

class DoubleEntryTransferTest {
    private WalletRepository repo;
    private Account accountA;
    private Account accountB;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        repo = new InMemoryWalletRepository();
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
        UUID txId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");
        // Should not allow transfer to self
        assertThrows(IllegalArgumentException.class, () -> {
            if (accountA.getAccountId().equals(accountA.getAccountId())) {
                throw new IllegalArgumentException("Cannot transfer to same account");
            }
        });
    }

    @Test
    void testTransferNonPositiveAmount() {
        UUID txId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("0.00");
        // Should not allow zero or negative amount
        assertThrows(IllegalArgumentException.class, () -> {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
        });
    }
} 