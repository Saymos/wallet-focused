package com.cubeia.wallet_focused.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);
    
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
            @ApiResponse(responseCode = "409", description = "Insufficient funds"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(@Valid @RequestBody TransferRequest request) {
        logger.info("Transfer request received: source={}, destination={}, amount={}, transactionId={}", 
                request.getSourceAccountId(), request.getDestinationAccountId(), 
                request.getAmount(), request.getTransactionId());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            walletService.transfer(request);
            
            // Success response
            response.put("success", true);
            if (request.getTransactionId() != null) {
                response.put("transactionId", request.getTransactionId().toString());
            }
            
            logger.info("Transfer completed successfully: transactionId={}", request.getTransactionId());
            return ResponseEntity.ok(response);
        } catch (InsufficientFundsException e) {
            logger.warn("Transfer failed - Insufficient funds: source={}, amount={}, transactionId={}", 
                    request.getSourceAccountId(), request.getAmount(), request.getTransactionId());
            
            // Handle insufficient funds
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.status(409).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Transfer failed - Invalid request: {}, transactionId={}", 
                    e.getMessage(), request.getTransactionId());
            
            // Handle invalid arguments
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during transfer: transactionId={}", 
                    request.getTransactionId(), e);
            
            // Handle unexpected errors
            response.put("success", false);
            response.put("error", "An unexpected error occurred");
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 