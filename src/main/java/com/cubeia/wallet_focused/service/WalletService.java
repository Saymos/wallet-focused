package com.cubeia.wallet_focused.service;

import com.cubeia.wallet_focused.model.TransferRequest;

public interface WalletService {
    void transfer(TransferRequest request);
} 