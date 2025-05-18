package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.InMemoryWalletRepository;
import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.model.WalletRepository;
import static com.cubeia.wallet_focused.service.TestConstants.SYSTEM_ACCOUNT_ID;

class WalletServiceImplTest {
    private WalletRepository repository;
    private AccountService accountService;
    private WalletService service;
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
    void testSuccessfulTransfer() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        service.transfer(request);
        
        // Check balances
        BigDecimal sourceBalance = accountService.calculateBalance(sourceId);
        BigDecimal destBalance = accountService.calculateBalance(destinationId);
        
        assertEquals(new BigDecimal("70.00"), sourceBalance);
        assertEquals(new BigDecimal("80.00"), destBalance);
        
        // Check transaction entries
        List<TransactionEntry> sourceTxs = repository.findTransactionsByAccount(sourceId);
        List<TransactionEntry> destTxs = repository.findTransactionsByAccount(destinationId);
        
        // Each account should have 2 entries (1 from setup + 1 from test)
        assertEquals(2, sourceTxs.size());
        assertEquals(2, destTxs.size());
        
        // Get the last transaction entry (the one from the test)
        TransactionEntry debit = sourceTxs.get(1);
        TransactionEntry credit = destTxs.get(1);
        
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
        
        // Check balance
        BigDecimal destBalance = accountService.calculateBalance(newDestinationId);
        assertEquals(amount, destBalance);
    }
    
    @Test
    void testInsufficientFunds() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.00"); // More than source has
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, 
            () -> service.transfer(request));
        assertEquals("Insufficient funds in source account", exception.getMessage());
        
        // Verify no changes were made
        BigDecimal sourceBalance = accountService.calculateBalance(sourceId);
        BigDecimal destBalance = accountService.calculateBalance(destinationId);
        
        assertEquals(new BigDecimal("100.00"), sourceBalance);
        assertEquals(new BigDecimal("50.00"), destBalance);
    }
    
    @Test
    void testTransferToSameAccount() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, sourceId, amount);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(request));
        assertEquals("Cannot transfer to same account", exception.getMessage());
    }
    
    @Test
    void testNegativeAmount() {
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("-10.00");
        
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(request));
        assertEquals("Amount must be positive", exception.getMessage());
    }
    
    @Test
    void testSourceAccountNotFound() {
        UUID transactionId = UUID.randomUUID();
        UUID nonExistentSourceId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");
        
        TransferRequest request = new TransferRequest(transactionId, nonExistentSourceId, destinationId, amount);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> service.transfer(request));
        assertEquals("Source account not found", exception.getMessage());
    }
    
    @Test
    void testIdempotentTransfer() {
        // Create a unique transaction ID for this test
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("25.00");
        
        // Create a transfer request
        TransferRequest request = new TransferRequest(transactionId, sourceId, destinationId, amount);
        
        // Execute the transfer once
        service.transfer(request);
        
        // Check balances after first transfer
        BigDecimal sourceBalanceAfterFirst = accountService.calculateBalance(sourceId);
        BigDecimal destBalanceAfterFirst = accountService.calculateBalance(destinationId);
        
        assertEquals(new BigDecimal("75.00"), sourceBalanceAfterFirst);
        assertEquals(new BigDecimal("75.00"), destBalanceAfterFirst);
        
        // Execute the same transfer (same transaction ID) again
        service.transfer(request);
        
        // Check balances after second transfer - should be unchanged
        BigDecimal sourceBalanceAfterSecond = accountService.calculateBalance(sourceId);
        BigDecimal destBalanceAfterSecond = accountService.calculateBalance(destinationId);
        
        // Balances should remain the same (idempotency)
        assertEquals(new BigDecimal("75.00"), sourceBalanceAfterSecond);
        assertEquals(new BigDecimal("75.00"), destBalanceAfterSecond);
        
        // Each account should have 3 entries (1 from setup + 1 from test + 0 from second attempt)
        List<TransactionEntry> sourceTxs = repository.findTransactionsByAccount(sourceId);
        List<TransactionEntry> destTxs = repository.findTransactionsByAccount(destinationId);
        
        assertEquals(2, sourceTxs.size());
        assertEquals(2, destTxs.size());
        
        // Verify the transaction entries match the single transfer
        TransactionEntry debit = null;
        TransactionEntry credit = null;
        
        // Find the entries with our transaction ID
        for (TransactionEntry entry : sourceTxs) {
            if (entry.getTransactionId().equals(transactionId)) {
                debit = entry;
                break;
            }
        }
        
        for (TransactionEntry entry : destTxs) {
            if (entry.getTransactionId().equals(transactionId)) {
                credit = entry;
                break;
            }
        }
        
        assertNotNull(debit, "Debit entry should exist");
        assertNotNull(credit, "Credit entry should exist");
        
        assertEquals(transactionId, debit.getTransactionId());
        assertEquals(transactionId, credit.getTransactionId());
        assertEquals(amount, debit.getAmount());
        assertEquals(amount, credit.getAmount());
        assertEquals(TransactionEntry.Type.DEBIT, debit.getType());
        assertEquals(TransactionEntry.Type.CREDIT, credit.getType());
        
        // Verify transaction is marked as processed
        assertTrue(repository.isTransactionProcessed(transactionId));
    }
    
    @Test
    void testUnexpectedExceptionDuringTransfer() {
        // Create a test repository that will throw an unexpected exception only for new transactions
        // but allow the setup transactions
        InMemoryWalletRepository realRepo = new InMemoryWalletRepository();
        WalletRepository mockRepository = new InMemoryWalletRepository() {
            @Override
            public void saveTransaction(TransactionEntry entry) {
                // Deliberately throw a runtime exception when saving new transactions
                // This will only be triggered during the actual transfer test
                throw new RuntimeException("Simulated database failure");
            }
            
            @Override
            public List<TransactionEntry> findTransactionsByAccount(UUID accountId) {
                // Delegate to the real repo for finding transactions
                return realRepo.findTransactionsByAccount(accountId);
            }
        };
        
        // Initialize source and destination accounts in both repositories
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        
        Account sourceAccount = new Account(sourceAccountId);
        mockRepository.saveAccount(sourceAccount);
        realRepo.saveAccount(sourceAccount);
        
        Account destAccount = new Account(destinationAccountId);
        mockRepository.saveAccount(destAccount);
        realRepo.saveAccount(destAccount);
        
        // Give source account sufficient funds using the real repo
        TransactionEntry initialCredit = new TransactionEntry(
            UUID.randomUUID(),
            sourceAccountId,
            SYSTEM_ACCOUNT_ID,
            new BigDecimal("100.00"),
            TransactionEntry.Type.CREDIT,
            Instant.now()
        );
        
        // Add the initial credit to the real repo
        realRepo.saveTransaction(initialCredit);
        
        // Create our service with the mock repository
        AccountService testAccountService = new AccountServiceImpl(mockRepository, null);
        WalletService testService = new WalletServiceImpl(mockRepository, testAccountService);
        
        // Create transfer request with an amount the account can afford
        UUID transactionId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");
        TransferRequest request = new TransferRequest(transactionId, sourceAccountId, destinationAccountId, amount);
        
        // Assert that the exception is propagated through the service layer
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testService.transfer(request);
        });
        
        assertEquals("Simulated database failure", exception.getMessage());
    }
} 