package com.cubeia.wallet_focused.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private UUID accountId;
    private UUID otherAccountId;
    private UUID transactionId;
    private List<TransactionEntry> transactions;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        accountId = UUID.randomUUID();
        otherAccountId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        
        // Create sample transactions
        transactions = new ArrayList<>();
        transactions.add(new TransactionEntry(
                transactionId,
                accountId,
                otherAccountId,
                new BigDecimal("50.00"),
                TransactionEntry.Type.DEBIT,
                Instant.parse("2025-01-01T10:00:00Z")
        ));
        
        transactions.add(new TransactionEntry(
                UUID.randomUUID(),
                accountId,
                otherAccountId,
                new BigDecimal("25.00"),
                TransactionEntry.Type.CREDIT,
                Instant.parse("2025-01-02T10:00:00Z")
        ));
    }

    @Test
    void getTransactions_AccountWithTransactions_ReturnsTransactionListAndStatus200() throws Exception {
        when(transactionService.accountExists(accountId)).thenReturn(true);
        when(transactionService.getTransactionsByAccount(accountId)).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/accounts/{id}/transactions", accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].type").value("DEBIT"))
                .andExpect(jsonPath("$[0].amount").value("50.0"))
                .andExpect(jsonPath("$[0].counterpartyId").value(otherAccountId.toString()));

        verify(transactionService).accountExists(accountId);
        verify(transactionService).getTransactionsByAccount(accountId);
    }

    @Test
    void getTransactions_AccountWithNoTransactions_ReturnsEmptyArrayAndStatus200() throws Exception {
        when(transactionService.accountExists(accountId)).thenReturn(true);
        when(transactionService.getTransactionsByAccount(accountId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/accounts/{id}/transactions", accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(transactionService).accountExists(accountId);
        verify(transactionService).getTransactionsByAccount(accountId);
    }

    @Test
    void getTransactions_NonExistentAccountId_ReturnsStatus404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(transactionService.accountExists(nonExistentId)).thenReturn(false);

        mockMvc.perform(get("/api/v1/accounts/{id}/transactions", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(transactionService).accountExists(nonExistentId);
        verify(transactionService, never()).getTransactionsByAccount(nonExistentId);
    }

    @Test
    void getTransactions_InvalidAccountIdFormat_ReturnsStatus400() throws Exception {
        String invalidId = "not-a-uuid";

        mockMvc.perform(get("/api/v1/accounts/{id}/transactions", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(transactionService);
    }
} 