package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.InMemoryWalletRepository;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.model.WalletRepository;
import static com.cubeia.wallet_focused.service.TestConstants.SYSTEM_ACCOUNT_ID;

class WalletServiceConcurrencyTest {
    private WalletRepository repository;
    private AccountService accountService;
    private WalletServiceImpl service;
    private UUID idA;
    private UUID idB;

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
        idA = UUID.randomUUID();
        idB = UUID.randomUUID();
        
        repository.saveAccount(new Account(idA));
        repository.saveAccount(new Account(idB));
        
        // Initialize account balances with transfers from system account
        TransferRequest aInitialTransfer = new TransferRequest(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            idA,
            new BigDecimal("1000.00")
        );
        
        TransferRequest bInitialTransfer = new TransferRequest(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            idB,
            new BigDecimal("1000.00")
        );
        
        service.transfer(aInitialTransfer);
        service.transfer(bInitialTransfer);
        
        // Verify initial balances are correct
        BigDecimal balanceA = accountService.calculateBalance(idA);
        BigDecimal balanceB = accountService.calculateBalance(idB);
        
        assertEquals(new BigDecimal("1000.00"), balanceA);
        assertEquals(new BigDecimal("1000.00"), balanceB);
    }

    @Test
    void testConcurrentTransfers() throws Exception {
        int numTransfers = 100;
        BigDecimal transferAmount = new BigDecimal("5.00");
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>();

        // Alternate transfers: A->B and B->A
        for (int i = 0; i < numTransfers; i++) {
            final boolean aToB = i % 2 == 0;
            final UUID txId = UUID.randomUUID();
            futures.add(executor.submit(() -> {
                if (aToB) {
                    service.transfer(new TransferRequest(txId, idA, idB, transferAmount));
                } else {
                    service.transfer(new TransferRequest(txId, idB, idA, transferAmount));
                }
            }));
        }
        for (Future<?> f : futures) {
            f.get();
        }
        executor.shutdown();
        
        // Final balances should be unchanged
        BigDecimal balanceA = accountService.calculateBalance(idA);
        BigDecimal balanceB = accountService.calculateBalance(idB);
        
        assertEquals(new BigDecimal("1000.00"), balanceA);
        assertEquals(new BigDecimal("1000.00"), balanceB);
        
        // Each account should have numTransfers + 1 entries (1 from setup + numTransfers from test)
        List<TransactionEntry> aTxs = repository.findTransactionsByAccount(idA);
        List<TransactionEntry> bTxs = repository.findTransactionsByAccount(idB);
        assertEquals(numTransfers + 1, aTxs.size());
        assertEquals(numTransfers + 1, bTxs.size());
    }
} 