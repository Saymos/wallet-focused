package com.cubeia.wallet_focused.controller;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cubeia.wallet_focused.config.GlobalExceptionHandler;
import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.service.AccountService;
import com.cubeia.wallet_focused.service.TransactionService;
import com.cubeia.wallet_focused.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class ValidationErrorTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private WalletService walletService;
    
    @Mock
    private AccountService accountService;
    
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransferController transferController;
    
    @InjectMocks
    private AccountController accountController;
    
    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        
        mockMvc = MockMvcBuilders.standaloneSetup(
                transferController, 
                accountController, 
                transactionController)
                .setControllerAdvice(exceptionHandler)
                .build();
                
        objectMapper = new ObjectMapper();
    }
    
    // Balance Endpoint Validation Tests
    
    @Test
    void getBalance_InvalidUUID_Returns400WithValidationError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/not-a-valid-uuid/balance")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getBalance_NonExistentAccount_Returns404() throws Exception {
        // Arrange
        UUID nonExistentAccountId = UUID.randomUUID();
        when(accountService.getAccount(nonExistentAccountId)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/" + nonExistentAccountId + "/balance")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    // Transaction Endpoint Validation Tests
    
    @Test
    void getTransactions_InvalidUUID_Returns400WithValidationError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/not-a-valid-uuid/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getTransactions_NonExistentAccount_Returns404() throws Exception {
        // Arrange - Use @Mock TransactionController instead of TransactionService
        UUID nonExistentAccountId = UUID.randomUUID();
        when(transactionService.accountExists(nonExistentAccountId)).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/" + nonExistentAccountId + "/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    // Transfer Endpoint Validation Tests
    
    @Test
    void transfer_MissingRequiredFields_Returns400WithValidationError() throws Exception {
        // Create a request missing all required fields
        String json = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verify(walletService, never()).transfer(any());
    }
    
    @Test
    void transfer_MissingTransactionId_Returns400WithValidationError() throws Exception {
        // Create a request missing transactionId
        String json = "{\"sourceAccountId\":\"" + UUID.randomUUID() + "\"," +
                "\"destinationAccountId\":\"" + UUID.randomUUID() + "\"," +
                "\"amount\":\"100.00\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
    
    @Test
    void transfer_MissingSourceAccountId_Returns400WithValidationError() throws Exception {
        // Create a request missing sourceAccountId
        String json = "{\"transactionId\":\"" + UUID.randomUUID() + "\"," +
                "\"destinationAccountId\":\"" + UUID.randomUUID() + "\"," +
                "\"amount\":\"100.00\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
    
    @Test
    void transfer_MissingDestinationAccountId_Returns400WithValidationError() throws Exception {
        // Create a request missing destinationAccountId
        String json = "{\"transactionId\":\"" + UUID.randomUUID() + "\"," +
                "\"sourceAccountId\":\"" + UUID.randomUUID() + "\"," +
                "\"amount\":\"100.00\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
    
    @Test
    void transfer_MissingAmount_Returns400WithValidationError() throws Exception {
        // Create a request missing amount
        String json = "{\"transactionId\":\"" + UUID.randomUUID() + "\"," +
                "\"sourceAccountId\":\"" + UUID.randomUUID() + "\"," +
                "\"destinationAccountId\":\"" + UUID.randomUUID() + "\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
    
    @Test
    void transfer_InvalidUUIDFormat_Returns400WithValidationError() throws Exception {
        // Create a request with invalid UUID formats
        String json = "{\"transactionId\":\"not-a-uuid\"," +
                "\"sourceAccountId\":\"invalid-source-id\"," +
                "\"destinationAccountId\":\"invalid-dest-id\"," +
                "\"amount\":\"100.00\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
    
    @Test
    void transfer_NegativeAmount_Returns400WithValidationError() throws Exception {
        // Create a request with negative amount
        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("-100.00")
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
    
    @Test
    void transfer_ZeroAmount_Returns400WithValidationError() throws Exception {
        // Create a request with zero amount
        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.ZERO
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
    
    @Test
    void transfer_SameSourceAndDestination_Returns400WithValidationError() throws Exception {
        // Create a request with same source and destination
        UUID sameId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                sameId,
                sameId,
                new BigDecimal("100.00")
        );
        
        doThrow(new IllegalArgumentException("Cannot transfer to same account"))
                .when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Cannot transfer to same account"));
    }
    
    @Test
    void transfer_InsufficientFunds_Returns400WithValidationError() throws Exception {
        // Create a valid request but with insufficient funds
        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("1000000.00") // Very large amount
        );
        
        doThrow(new InsufficientFundsException("Insufficient funds in source account"))
                .when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Insufficient funds in source account"));
    }
    
    @Test
    void transfer_NonExistentSourceAccount_Returns400WithValidationError() throws Exception {
        // Create a request with a non-existent source account
        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(), // Non-existent source account
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );
        
        doThrow(new IllegalArgumentException("Source account not found"))
                .when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Source account not found"));
    }
} 