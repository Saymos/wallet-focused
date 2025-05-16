package com.cubeia.wallet_focused.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.model.WalletRepository;

@Service
public class WalletServiceImpl implements WalletService {
    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    
    private final WalletRepository repository;
    private final Map<UUID, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    public WalletServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    private ReentrantLock getLock(UUID accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

    @Override
    public void transfer(TransferRequest request) {
        logger.info("Starting transfer operation: transactionId={}, sourceAccountId={}, destinationAccountId={}, amount={}",
                request.getTransactionId(), request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount());
        
        // Idempotency check
        if (repository.isTransactionProcessed(request.getTransactionId())) {
            logger.info("Transfer already processed (idempotency): transactionId={}", request.getTransactionId());
            return;
        }
        
        // Lock both accounts in consistent order
        UUID id1 = request.getSourceAccountId();
        UUID id2 = request.getDestinationAccountId();
        ReentrantLock lock1, lock2;
        if (id1.compareTo(id2) < 0) {
            lock1 = getLock(id1);
            lock2 = getLock(id2);
        } else {
            lock1 = getLock(id2);
            lock2 = getLock(id1);
        }
        
        logger.debug("Acquiring account locks for transfer: transactionId={}", request.getTransactionId());
        lock1.lock();
        lock2.lock();
        try {
            // Validate inputs
            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Invalid transfer amount: transactionId={}, amount={}", 
                        request.getTransactionId(), request.getAmount());
                throw new IllegalArgumentException("Amount must be positive");
            }
            if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
                logger.warn("Transfer to same account attempted: accountId={}, transactionId={}", 
                        request.getSourceAccountId(), request.getTransactionId());
                throw new IllegalArgumentException("Cannot transfer to same account");
            }
            
            // Find source account
            Account sourceAccount = repository.findAccount(request.getSourceAccountId());
            if (sourceAccount == null) {
                logger.warn("Source account not found: accountId={}, transactionId={}", 
                        request.getSourceAccountId(), request.getTransactionId());
                throw new IllegalArgumentException("Source account not found");
            }
            
            // Check sufficient funds
            if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
                logger.warn("Insufficient funds in source account: accountId={}, balance={}, requestedAmount={}, transactionId={}", 
                        sourceAccount.getAccountId(), sourceAccount.getBalance(), request.getAmount(), request.getTransactionId());
                throw new InsufficientFundsException("Insufficient funds in source account");
            }
            
            // Find or create destination account
            Account destinationAccount = repository.findAccount(request.getDestinationAccountId());
            if (destinationAccount == null) {
                logger.info("Creating new destination account: accountId={}", request.getDestinationAccountId());
                destinationAccount = new Account(request.getDestinationAccountId(), BigDecimal.ZERO);
                repository.saveAccount(destinationAccount);
            }
            
            // Create transaction entries
            Instant timestamp = Instant.now();
            TransactionEntry debitEntry = new TransactionEntry(
                request.getTransactionId(),
                sourceAccount.getAccountId(),
                destinationAccount.getAccountId(),
                request.getAmount(),
                TransactionEntry.Type.DEBIT,
                timestamp
            );
            TransactionEntry creditEntry = new TransactionEntry(
                request.getTransactionId(),
                destinationAccount.getAccountId(),
                sourceAccount.getAccountId(),
                request.getAmount(),
                TransactionEntry.Type.CREDIT,
                timestamp
            );
            
            // Update balances
            BigDecimal sourceBalanceBefore = sourceAccount.getBalance();
            BigDecimal destBalanceBefore = destinationAccount.getBalance();
            
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
            
            logger.debug("Updating account balances: source [{}] {} -> {}, destination [{}] {} -> {}", 
                    sourceAccount.getAccountId(), sourceBalanceBefore, sourceAccount.getBalance(),
                    destinationAccount.getAccountId(), destBalanceBefore, destinationAccount.getBalance());
            
            // Save everything
            repository.saveAccount(sourceAccount);
            repository.saveAccount(destinationAccount);
            repository.saveTransaction(debitEntry);
            repository.saveTransaction(creditEntry);
            repository.markTransactionProcessed(request.getTransactionId());
            
            logger.info("Transfer completed successfully: transactionId={}, amount={}, source={}, destination={}", 
                    request.getTransactionId(), request.getAmount(), 
                    sourceAccount.getAccountId(), destinationAccount.getAccountId());
        } catch (InsufficientFundsException e) {
            logger.warn("Transfer failed - Insufficient funds: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.warn("Transfer failed - Invalid request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during transfer: transactionId={}", request.getTransactionId(), e);
            throw e;
        } finally {
            logger.debug("Releasing account locks: transactionId={}", request.getTransactionId());
            lock2.unlock();
            lock1.unlock();
        }
    }
} 