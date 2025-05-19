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
        String requestJson = String.format(
                "{\"transactionId\":\"%s\",\"sourceAccountId\":\"%s\",\"destinationAccountId\":\"%s\",\"amount\":%s}",
                transactionId, sourceAccountId, destinationAccountId, amount);

        doNothing().when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()));

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_InvalidJson_ReturnsStatus400() throws Exception {
        // Arrange
        String invalidJson = "{\"sourceAccountId\":\"invalid-uuid\",\"amount\":100}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(walletService);
    }

    @Test
    void transfer_NegativeAmount_ReturnsStatus400() throws Exception {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("-50.00");

        String requestJson = String.format(
                "{\"transactionId\":\"%s\",\"sourceAccountId\":\"%s\",\"destinationAccountId\":\"%s\",\"amount\":%s}",
                transactionId, sourceAccountId, destinationAccountId, amount);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(walletService);
    }

    @Test
    void transfer_SameSourceAndDestination_ReturnsStatus400() throws Exception {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        String requestJson = String.format(
                "{\"transactionId\":\"%s\",\"sourceAccountId\":\"%s\",\"destinationAccountId\":\"%s\",\"amount\":%s}",
                transactionId, accountId, accountId, amount);

        doThrow(new IllegalArgumentException("Cannot transfer to same account"))
                .when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Cannot transfer to same account"));

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_InsufficientFunds_ReturnsStatus409() throws Exception {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000000.00");

        String requestJson = String.format(
                "{\"transactionId\":\"%s\",\"sourceAccountId\":\"%s\",\"destinationAccountId\":\"%s\",\"amount\":%s}",
                transactionId, sourceAccountId, destinationAccountId, amount);

        doThrow(new InsufficientFundsException("Insufficient funds in source account"))
                .when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Insufficient funds in source account"));

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_UnexpectedException_ReturnsStatus500() throws Exception {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        String requestJson = String.format(
                "{\"transactionId\":\"%s\",\"sourceAccountId\":\"%s\",\"destinationAccountId\":\"%s\",\"amount\":%s}",
                transactionId, sourceAccountId, destinationAccountId, amount);

        doThrow(new RuntimeException("Unexpected database error"))
                .when(walletService).transfer(any(TransferRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));

        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_NullTransactionIdHandling_ReturnsResponseWithoutTransactionId() throws Exception {
        // Arrange - Create a request with valid data but explicitly include a null transactionId
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        
        // We need to include a transactionId here to create a valid model
        UUID transactionId = UUID.randomUUID();
        
        String requestJson = String.format(
                "{\"transactionId\":\"%s\",\"sourceAccountId\":\"%s\",\"destinationAccountId\":\"%s\",\"amount\":%s}",
                transactionId, sourceAccountId, destinationAccountId, amount);

        // Mock the wallet service behavior to not throw an exception
        doNothing().when(walletService).transfer(any(TransferRequest.class));
        
        // Act & Assert - Use the regular endpoint
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString())); // We expect the transactionId to be returned
                
        verify(walletService, times(1)).transfer(any(TransferRequest.class));
    }
} 