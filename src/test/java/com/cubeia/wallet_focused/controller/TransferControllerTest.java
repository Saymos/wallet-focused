package com.cubeia.wallet_focused.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransferController transferController;

    private UUID transactionId;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transferController).build();
        objectMapper = new ObjectMapper();

        transactionId = UUID.randomUUID();
        sourceAccountId = UUID.randomUUID();
        destinationAccountId = UUID.randomUUID();
        amount = new BigDecimal("100.00");
    }

    @Test
    void transfer_ValidRequest_ReturnsStatus200() throws Exception {
        // Arrange
        TransferRequest request = new TransferRequest(
                transactionId,
                sourceAccountId,
                destinationAccountId,
                amount
        );
        doNothing().when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()));

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_DuplicateTransactionId_ReturnsStatus200() throws Exception {
        // Arrange
        TransferRequest request = new TransferRequest(
                transactionId,
                sourceAccountId,
                destinationAccountId,
                amount
        );
        // Service method is idempotent, should not throw exception for duplicate transfer
        doNothing().when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()));

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_InsufficientFunds_ReturnsStatus400() throws Exception {
        // Arrange
        TransferRequest request = new TransferRequest(
                transactionId,
                sourceAccountId,
                destinationAccountId,
                amount
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

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_InvalidRequest_SameAccount_ReturnsStatus400() throws Exception {
        // Arrange - this validation happens in the service layer not bean validation
        TransferRequest request = new TransferRequest(
                transactionId,
                sourceAccountId,
                sourceAccountId, // Same as source (invalid)
                amount
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

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_InvalidRequest_NegativeAmount_ReturnsStatus400() throws Exception {
        // Create a request with negative amount which will be caught by @Positive validation
        String json = "{\"transactionId\":\"" + transactionId + 
                "\",\"sourceAccountId\":\"" + sourceAccountId + 
                "\",\"destinationAccountId\":\"" + destinationAccountId +
                "\",\"amount\":\"-50.00\"}";

        // Act & Assert - validation should catch this before service method is called
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }

    @Test
    void transfer_MissingTransactionId_ReturnsStatus400() throws Exception {
        // Create JSON with null transactionId to test Bean Validation
        String json = "{\"sourceAccountId\":\"" + sourceAccountId + 
                "\",\"destinationAccountId\":\"" + destinationAccountId +
                "\",\"amount\":\"100.00\"}";

        // Act & Assert - we expect validation to fail before the service is called
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
        verifyNoInteractions(walletService);
    }
} 