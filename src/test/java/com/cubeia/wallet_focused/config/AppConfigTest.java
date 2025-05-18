package com.cubeia.wallet_focused.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.cubeia.wallet_focused.model.InMemoryWalletRepository;
import com.cubeia.wallet_focused.model.WalletRepository;

public class AppConfigTest {

    @Test
    public void testWalletRepositoryBeanCreation() {
        // Create the config manually
        AppConfig appConfig = new AppConfig();
        
        // Act
        WalletRepository repositoryFromConfig = appConfig.walletRepository();
        
        // Assert
        assertNotNull(repositoryFromConfig);
        assertTrue(repositoryFromConfig instanceof InMemoryWalletRepository);
    }
} 