package com.cubeia.wallet_focused.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

public class OpenApiConfigTest {

    @Test
    public void testWalletOpenAPIConfiguration() {
        // Arrange
        OpenApiConfig openApiConfig = new OpenApiConfig();
        
        // Act
        OpenAPI openAPI = openApiConfig.walletOpenAPI();
        
        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Wallet Service API", openAPI.getInfo().getTitle());
        assertEquals("v1.0", openAPI.getInfo().getVersion());
        
        // Verify servers
        assertNotNull(openAPI.getServers());
        assertEquals(1, openAPI.getServers().size());
        assertEquals("/", openAPI.getServers().get(0).getUrl());
        
        // Verify components and schemas
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSchemas());
        assertEquals(5, openAPI.getComponents().getSchemas().size());
        assertNotNull(openAPI.getComponents().getSchemas().get("Account"));
        assertNotNull(openAPI.getComponents().getSchemas().get("TransactionEntry"));
        assertNotNull(openAPI.getComponents().getSchemas().get("TransferRequestDTO"));
        assertNotNull(openAPI.getComponents().getSchemas().get("ErrorResponse"));
        assertNotNull(openAPI.getComponents().getSchemas().get("ValidationErrorResponse"));
    }
} 