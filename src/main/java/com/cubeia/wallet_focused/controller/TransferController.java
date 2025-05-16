package com.cubeia.wallet_focused.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.model.TransferRequest;
import com.cubeia.wallet_focused.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Transfer", description = "Fund transfer operations")
public class TransferController {
    private final WalletService walletService;

    public TransferController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(summary = "Transfer funds between accounts", 
            description = "Transfers funds from source account to destination account using double-entry bookkeeping")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer successful",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request (negative amount, same account, etc.)"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient funds")
    })
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(@Valid @RequestBody TransferRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            walletService.transfer(request);
            
            // Success response
            response.put("success", true);
            if (request.getTransactionId() != null) {
                response.put("transactionId", request.getTransactionId().toString());
            }
            
            return ResponseEntity.ok(response);
        } catch (InsufficientFundsException | IllegalArgumentException e) {
            // Handle insufficient funds
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
        // Handle validation errors
        
    }
} 