package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.InMemoryWalletRepository;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.model.WalletRepository;
import static com.cubeia.wallet_focused.service.TestConstants.SYSTEM_ACCOUNT_ID;

class WalletServiceIdempotencyTest {
    private WalletRepository repository;
    private AccountService accountService;
    private WalletServiceImpl service;
    private UUID sourceId;
    private UUID destinationId;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWalletRepository();
        accountService = new AccountServiceImpl(repository, null);
        service = new WalletServiceImpl(repository, accountService);
        
        // Create system account (unlimited funds)
        repository.saveAccount(new Account(SYSTEM_ACCOUNT_ID));
        
        // Create initial credit to system account
        Instant now = Instant.now();
        TransactionEntry systemCredit = new TransactionEntry(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            SYSTEM_ACCOUNT_ID,
            new BigDecimal("1000000.00"),
            TransactionEntry.Type.CREDIT,
            now
        );
        repository.saveTransaction(systemCredit);
        
        // Create test accounts
        sourceId = UUID.randomUUID();
        destinationId = UUID.randomUUID();
        
        repository.saveAccount(new Account(sourceId));
        repository.saveAccount(new Account(destinationId));
        
        // Initialize account balances with transfers from system account
        TransferRequest sourceInitialTransfer = new TransferRequest(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            sourceId,
            new BigDecimal("100.00")
        );
        
        TransferRequest destInitialTransfer = new TransferRequest(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            destinationId,
            new BigDecimal("50.00")
        );
        
        service.transfer(sourceInitialTransfer);
        service.transfer(destInitialTransfer);
        
        // Verify initial balances are correct
        BigDecimal sourceBalance = accountService.calculateBalance(sourceId);
        BigDecimal destBalance = accountService.calculateBalance(destinationId);
        
        assertEquals(new BigDecimal("100.00"), sourceBalance);
        assertEquals(new BigDecimal("50.00"), destBalance);
    }

    @Test
    void testIdempotentTransfer() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        
        // First call
        service.transfer(request);
        
        // Second call (should be idempotent)
        service.transfer(request);
        
        // Balances should only reflect one transfer
        BigDecimal sourceBalance = accountService.calculateBalance(sourceId);
        BigDecimal destBalance = accountService.calculateBalance(destinationId);
        
        assertEquals(new BigDecimal("70.00"), sourceBalance);
        assertEquals(new BigDecimal("80.00"), destBalance);
        
        // Count the transaction entries with our transaction ID
        List<TransactionEntry> sourceTxs = repository.findTransactionsByAccount(sourceId);
        List<TransactionEntry> destTxs = repository.findTransactionsByAccount(destinationId);
        
        // Each account should have 2 entries (1 from setup + 1 from test)
        assertEquals(2, sourceTxs.size());
        assertEquals(2, destTxs.size());
        
        // Count entries with our specific transaction ID (should be exactly 1)
        int sourceEntriesWithTxId = 0;
        int destEntriesWithTxId = 0;
        
        for (TransactionEntry entry : sourceTxs) {
            if (entry.getTransactionId().equals(transactionId)) {
                sourceEntriesWithTxId++;
            }
        }
        
        for (TransactionEntry entry : destTxs) {
            if (entry.getTransactionId().equals(transactionId)) {
                destEntriesWithTxId++;
            }
        }
        
        assertEquals(1, sourceEntriesWithTxId, "Should have exactly one entry with the test transaction ID");
        assertEquals(1, destEntriesWithTxId, "Should have exactly one entry with the test transaction ID");
    }
} 