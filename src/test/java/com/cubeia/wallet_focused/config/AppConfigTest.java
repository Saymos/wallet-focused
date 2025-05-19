package com.cubeia.wallet_focused.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cubeia.wallet_focused.model.InMemoryWalletRepository;
import com.cubeia.wallet_focused.model.WalletRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RepositoryConfigTest {

    @Autowired
    private ApplicationContext context;
    
    @Test
    public void testWalletRepositoryBeanExistsInContext() {
        // Verify that the WalletRepository bean exists and is an instance of InMemoryWalletRepository
        WalletRepository repository = context.getBean(WalletRepository.class);
        
        // Assert
        assertNotNull(repository);
        assertTrue(repository instanceof InMemoryWalletRepository);
    }
} 