package com.cubeia.wallet_focused.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.service.AccountService;
import com.cubeia.wallet_focused.service.AccountServiceImpl;

class WalletRepositoryTest {
    private WalletRepository repo;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        repo = new InMemoryWalletRepository();
        accountService = new AccountServiceImpl(repo);
    }

    @Test
    void testSaveAndFindAccount() {
        UUID id = UUID.randomUUID();
        Account account = new Account(id);
        repo.saveAccount(account);
        
        // Add a transaction to give it a balance
        TransactionEntry entry = new TransactionEntry(
            UUID.randomUUID(),
            id,
            UUID.randomUUID(),
            new BigDecimal("100.00"),
            TransactionEntry.Type.CREDIT,
            Instant.now()
        );
        repo.saveTransaction(entry);
        
        Account found = repo.findAccount(id);
        assertNotNull(found);
        assertEquals(id, found.getAccountId());
        
        // Check balance using the account service
        BigDecimal balance = accountService.calculateBalance(id);
        assertEquals(new BigDecimal("100.00"), balance);
    }

    @Test
    void testFindTransactionsByAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId);
        repo.saveAccount(account);
        
        TransactionEntry entry = new TransactionEntry(
            UUID.randomUUID(),
            accountId,
            UUID.randomUUID(),
            new BigDecimal("10.00"),
            TransactionEntry.Type.CREDIT,
            Instant.now()
        );
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