package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
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

class WalletServiceConcurrencyTest {
    private WalletRepository repository;
    private WalletServiceImpl service;
    private Account accountA;
    private Account accountB;
    private UUID idA;
    private UUID idB;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWalletRepository();
        service = new WalletServiceImpl(repository);
        idA = UUID.randomUUID();
        idB = UUID.randomUUID();
        accountA = new Account(idA, new BigDecimal("1000.00"));
        accountB = new Account(idB, new BigDecimal("1000.00"));
        repository.saveAccount(accountA);
        repository.saveAccount(accountB);
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
        assertEquals(new BigDecimal("1000.00"), repository.findAccount(idA).getBalance());
        assertEquals(new BigDecimal("1000.00"), repository.findAccount(idB).getBalance());
        // Each account should have numTransfers debits or credits
        List<TransactionEntry> aTxs = repository.findTransactionsByAccount(idA);
        List<TransactionEntry> bTxs = repository.findTransactionsByAccount(idB);
        assertEquals(numTransfers, aTxs.size());
        assertEquals(numTransfers, bTxs.size());
    }
} 