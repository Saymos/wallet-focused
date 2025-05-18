package com.cubeia.wallet_focused.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubeia.wallet_focused.config.GlobalExceptionHandler;
import com.cubeia.wallet_focused.dto.TransferRequestDTO;
import com.cubeia.wallet_focused.dto.TransferResponseDTO;
import com.cubeia.wallet_focused.model.InsufficientFundsException;
import com.cubeia.wallet_focused.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
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
                    content = @Content(schema = @Schema(implementation = TransferResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request (negative amount, same account, etc.)",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Insufficient funds",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/accounts/transfer")
    public ResponseEntity<TransferResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO requestDTO) {
        logger.info("Transfer request received: source={}, destination={}, amount={}, transactionId={}", 
                requestDTO.sourceAccountId(), requestDTO.destinationAccountId(), 
                requestDTO.amount(), requestDTO.transactionId());
        
        try {
            // Convert DTO to domain model using the toModel method
            walletService.transfer(requestDTO.toModel());
            
            // Success response
            TransferResponseDTO response = TransferResponseDTO.success(requestDTO.transactionId());
            
            logger.info("Transfer completed successfully: transactionId={}", requestDTO.transactionId());
            return ResponseEntity.ok(response);
        } catch (InsufficientFundsException e) {
            logger.warn("Transfer failed - Insufficient funds: source={}, amount={}, transactionId={}", 
                    requestDTO.sourceAccountId(), requestDTO.amount(), requestDTO.transactionId());
            
            // Handle insufficient funds
            TransferResponseDTO response = TransferResponseDTO.error(e.getMessage());
            return ResponseEntity.status(409).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Transfer failed - Invalid request: {}, transactionId={}", 
                    e.getMessage(), requestDTO.transactionId());
            
            // Handle invalid arguments
            TransferResponseDTO response = TransferResponseDTO.error(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during transfer: transactionId={}", 
                    requestDTO.transactionId(), e);
            
            // Handle unexpected errors
            TransferResponseDTO response = TransferResponseDTO.error("An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }
} 