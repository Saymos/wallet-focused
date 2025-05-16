package com.cubeia.wallet_focused.controller;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.service.AccountService;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;
    
    private UUID accountId;
    private Account account;
    private BigDecimal balance;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        
        // Create a test account and balance
        accountId = UUID.randomUUID();
        balance = new BigDecimal("100.50");
        account = new Account(accountId);
    }

    @Test
    void getBalance_ValidAccountId_ReturnsBalanceAndStatus200() throws Exception {
        // Setup mocks
        when(accountService.getAccount(accountId)).thenReturn(Optional.of(account));
        when(accountService.calculateBalance(accountId)).thenReturn(balance);

        // Perform test
        mockMvc.perform(get("/api/v1/accounts/{id}/balance", accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value("100.5"));

        // Verify interactions
        verify(accountService).getAccount(accountId);
        verify(accountService).calculateBalance(accountId);
    }

    @Test
    void getBalance_NonExistentAccountId_ReturnsStatus404() throws Exception {
        // Setup mock
        when(accountService.getAccount(accountId)).thenReturn(Optional.empty());

        // Perform test
        mockMvc.perform(get("/api/v1/accounts/{id}/balance", accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verify interactions
        verify(accountService).getAccount(accountId);
    }

    @Test
    void getBalance_InvalidAccountIdFormat_ReturnsStatus400() throws Exception {
        String invalidId = "not-a-uuid";

        mockMvc.perform(get("/api/v1/accounts/{id}/balance", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
} 