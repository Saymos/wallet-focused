package com.cubeia.wallet_focused.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubeia.wallet_focused.dto.TransactionEntryDTO;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Transaction", description = "Account transaction operations")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Get transactions for account", description = "Retrieves all transaction entries for a specified account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionEntryDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid account ID format"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionEntryDTO>> getTransactions(@PathVariable("id") String id) {
        logger.info("Transactions request received for account ID: {}", id);
        
        UUID accountId;
        try {
            accountId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid account ID format: {}", id);
            return ResponseEntity.badRequest().build();
        }

        // Check if the account exists
        if (!transactionService.accountExists(accountId)) {
            logger.info("Account not found: {}", accountId);
            return ResponseEntity.notFound().build();
        }

        List<TransactionEntry> transactions = transactionService.getTransactionsByAccount(accountId);
        
        // Convert model objects to DTOs
        List<TransactionEntryDTO> transactionDTOs = transactions.stream()
                .map(TransactionEntryDTO::fromModel)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} transactions for account: {}", transactionDTOs.size(), accountId);
        return ResponseEntity.ok(transactionDTOs);
    }
} 