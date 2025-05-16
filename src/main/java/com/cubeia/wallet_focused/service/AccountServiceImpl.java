package com.cubeia.wallet_focused.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.WalletRepository;

@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    
    private final WalletRepository repository;

    public AccountServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> getAccount(UUID accountId) {
        logger.debug("Fetching account: accountId={}", accountId);
        Account account = repository.findAccount(accountId);
        
        if (account == null) {
            logger.info("Account not found: accountId={}", accountId);
            return Optional.empty();
        }
        
        logger.debug("Account found: accountId={}, balance={}", accountId, account.getBalance());
        return Optional.of(account);
    }
} 