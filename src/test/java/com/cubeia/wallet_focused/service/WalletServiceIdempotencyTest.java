package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
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

class WalletServiceIdempotencyTest {
    private WalletRepository repository;
    private WalletServiceImpl service;
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
    void testIdempotentTransfer() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30.00");
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        // First call
        service.transfer(request);
        // Second call (should be idempotent)
        service.transfer(request);
        // Balances should only reflect one transfer
        assertEquals(new BigDecimal("70.00"), repository.findAccount(sourceId).getBalance());
        assertEquals(new BigDecimal("80.00"), repository.findAccount(destinationId).getBalance());
        // Only one debit and one credit entry for this transactionId
        List<TransactionEntry> sourceTxs = repository.findTransactionsByAccount(sourceId);
        List<TransactionEntry> destTxs = repository.findTransactionsByAccount(destinationId);
        assertEquals(1, sourceTxs.size());
        assertEquals(1, destTxs.size());
        assertEquals(transactionId, sourceTxs.get(0).getTransactionId());
        assertEquals(transactionId, destTxs.get(0).getTransactionId());
    }
} 