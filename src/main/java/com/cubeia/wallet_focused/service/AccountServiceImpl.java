package com.cubeia.wallet_focused.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.WalletRepository;

@Service
public class AccountServiceImpl implements AccountService {
    private final WalletRepository repository;

    public AccountServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> getAccount(UUID accountId) {
        Account account = repository.findAccount(accountId);
        return Optional.ofNullable(account);
    }
} 