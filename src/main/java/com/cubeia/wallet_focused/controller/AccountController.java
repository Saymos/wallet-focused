package com.cubeia.wallet_focused.controller;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubeia.wallet_focused.dto.AccountDTO;
import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

/**
 * REST controller for account-related operations.
 * Provides endpoints for retrieving account balances.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account", description = "Account balance operations")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    private final AccountService accountService;

    /**
     * Creates a new AccountController with the specified account service.
     *
     * @param accountService the account service to use
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets the balance for an account.
     *
     * @param id the account ID as a string
     * @return a response with the account ID and balance, or an error response
     */
    @Operation(summary = "Get account balance", description = "Retrieves the current balance for a specified account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account balance found", content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid account ID format"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountDTO> getBalance(@PathVariable("id") String id) {
        logger.info("Balance request received for account ID: {}", id);
        
        UUID accountId;
        try {
            accountId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid account ID format: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            // Get account and calculate balance using event sourcing
            Optional<Account> accountOpt = accountService.getAccount(accountId);
            if (accountOpt.isEmpty()) {
                logger.info("Account not found: {}", accountId);
                return ResponseEntity.notFound().build();
            }
            
            Account account = accountOpt.get();
            BigDecimal balance = accountService.calculateBalance(accountId);
            
            // Create DTO for response
            AccountDTO response = new AccountDTO(account.getAccountId(), balance);
            
            logger.info("Balance returned successfully for account: {}", accountId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            logger.info("Account not found: {}", accountId);
            return ResponseEntity.notFound().build();
        }
    }
} 