package com.cubeia.wallet_focused.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cubeia.wallet_focused.model.InMemoryWalletRepository;
import com.cubeia.wallet_focused.model.WalletRepository;

@Configuration
public class AppConfig {

    @Bean
    public WalletRepository walletRepository() {
        return new InMemoryWalletRepository();
    }
} 