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

import com.cubeia.wallet_focused.service.AccountService;
import com.cubeia.wallet_focused.service.AccountServiceImpl;
import static com.cubeia.wallet_focused.service.TestConstants.SYSTEM_ACCOUNT_ID;
import com.cubeia.wallet_focused.service.WalletService;
import com.cubeia.wallet_focused.service.WalletServiceImpl;

class DoubleEntryTransferTest {
    private WalletRepository repo;
    private AccountService accountService;
    private WalletService walletService;
    private UUID accountIdA;
    private UUID accountIdB;

    @BeforeEach
    void setUp() {
        repo = new InMemoryWalletRepository();
        accountService = new AccountServiceImpl(repo, null);
        walletService = new WalletServiceImpl(repo, accountService);
        
        // Create system account with initial funds
        repo.saveAccount(new Account(SYSTEM_ACCOUNT_ID));
        Instant now = Instant.now();
        TransactionEntry systemCredit = new TransactionEntry(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            SYSTEM_ACCOUNT_ID,
            new BigDecimal("1000000.00"),
            TransactionEntry.Type.CREDIT,
            now
        );
        repo.saveTransaction(systemCredit);
        
        // Create test accounts
        accountIdA = UUID.randomUUID();
        accountIdB = UUID.randomUUID();
        
        repo.saveAccount(new Account(accountIdA));
        repo.saveAccount(new Account(accountIdB));
        
        // Setup initial balances through transactions
        TransferRequest requestA = new TransferRequest(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            accountIdA,
            new BigDecimal("100.00")
        );
        
        TransferRequest requestB = new TransferRequest(
            UUID.randomUUID(),
            SYSTEM_ACCOUNT_ID,
            accountIdB,
            new BigDecimal("50.00")
        );
        
        walletService.transfer(requestA);
        walletService.transfer(requestB);
        
        // Verify initial balances
        assertEquals(new BigDecimal("100.00"), accountService.calculateBalance(accountIdA));
        assertEquals(new BigDecimal("50.00"), accountService.calculateBalance(accountIdB));
    }

    @Test
    void testDoubleEntryTransfer() {
        UUID txId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30.00");
        
        // Perform transfer using the wallet service
        TransferRequest request = new TransferRequest(
            txId,
            accountIdA,
            accountIdB,
            amount
        );
        
        walletService.transfer(request);
        
        // Assert balances
        assertEquals(new BigDecimal("70.00"), accountService.calculateBalance(accountIdA));
        assertEquals(new BigDecimal("80.00"), accountService.calculateBalance(accountIdB));
        
        // Assert ledger entries
        List<TransactionEntry> aTxs = repo.findTransactionsByAccount(accountIdA);
        List<TransactionEntry> bTxs = repo.findTransactionsByAccount(accountIdB);
        
        assertTrue(aTxs.stream().anyMatch(e -> e.getType() == TransactionEntry.Type.DEBIT && e.getTransactionId().equals(txId)));
        assertTrue(bTxs.stream().anyMatch(e -> e.getType() == TransactionEntry.Type.CREDIT && e.getTransactionId().equals(txId)));
    }

    @Test
    void testTransferSameAccount() {
        // Create a transfer request with same source and destination account
        TransferRequest request = new TransferRequest(
            UUID.randomUUID(),
            accountIdA,
            accountIdA, // Same account as source
            new BigDecimal("10.00")
        );
        
        // Assert that trying to transfer to the same account throws an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.transfer(request);
        });
        
        // Verify exception message matches the implementation
        assertEquals("Cannot transfer to same account", exception.getMessage());
        
        // Verify account balance remains unchanged
        assertEquals(new BigDecimal("100.00"), accountService.calculateBalance(accountIdA));
    }

    @Test
    void testTransferNonPositiveAmount() {
        // Create a transfer request with zero amount
        TransferRequest zeroAmountRequest = new TransferRequest(
            UUID.randomUUID(),
            accountIdA,
            accountIdB,
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
            accountIdA,
            accountIdB,
            new BigDecimal("-10.00") // Negative amount
        );
        
        // Assert that trying to transfer negative amount throws an exception
        IllegalArgumentException negativeException = assertThrows(IllegalArgumentException.class, () -> {
            walletService.transfer(negativeAmountRequest);
        });
        
        // Verify exception message
        assertEquals("Amount must be positive", negativeException.getMessage());
        
        // Verify account balances remain unchanged
        assertEquals(new BigDecimal("100.00"), accountService.calculateBalance(accountIdA));
        assertEquals(new BigDecimal("50.00"), accountService.calculateBalance(accountIdB));
    }
} 