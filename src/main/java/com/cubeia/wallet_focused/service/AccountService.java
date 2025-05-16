package com.cubeia.wallet_focused.service;

import java.util.Optional;
import java.util.UUID;

import com.cubeia.wallet_focused.model.Account;

public interface AccountService {
    Optional<Account> getAccount(UUID accountId);
} 