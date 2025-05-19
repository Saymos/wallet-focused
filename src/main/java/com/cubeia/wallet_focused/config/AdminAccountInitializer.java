package com.cubeia.wallet_focused.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.cubeia.wallet_focused.model.InMemoryWalletRepository;

/**
 * Configuration class that initializes the admin account with funds at application startup.
 */
@Configuration
public class AdminAccountInitializer {
    
    private final InMemoryWalletRepository walletRepository;
    
    @Autowired
    public AdminAccountInitializer(InMemoryWalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    
    /**
     * Initialize the admin account when the application context is ready.
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        walletRepository.initializeAdminAccountIfNeeded();
    }
} 