package com.cubeia.wallet_focused.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletRepositoryTest {
    private WalletRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryWalletRepository();
    }

    @Test
    void testSaveAndFindAccount() {
        UUID id = UUID.randomUUID();
        Account account = new Account(id, new BigDecimal("100.00"));
        repo.saveAccount(account);
        Account found = repo.findAccount(id);
        assertNotNull(found);
        assertEquals(id, found.getAccountId());
        assertEquals(new BigDecimal("100.00"), found.getBalance());
    }

    @Test
    void testFindTransactionsByAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, BigDecimal.ZERO);
        repo.saveAccount(account);
        TransactionEntry entry = new TransactionEntry(UUID.randomUUID(), accountId, UUID.randomUUID(), new BigDecimal("10.00"), TransactionEntry.Type.CREDIT, Instant.now());
        repo.saveTransaction(entry);
        List<TransactionEntry> txs = repo.findTransactionsByAccount(accountId);
        assertEquals(1, txs.size());
        assertEquals(entry.getTransactionId(), txs.get(0).getTransactionId());
    }

    @Test
    void testMarkTransactionProcessed() {
        UUID txId = UUID.randomUUID();
        assertFalse(repo.isTransactionProcessed(txId));
        repo.markTransactionProcessed(txId);
        assertTrue(repo.isTransactionProcessed(txId));
    }
} 