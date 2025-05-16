package com.cubeia.wallet_focused.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account", description = "Account balance operations")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Get account balance", description = "Retrieves the current balance for a specified account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account balance found",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid account ID format"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable("id") String id) {
        UUID accountId;
        try {
            accountId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Account> account = accountService.getAccount(accountId);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", account.get().getAccountId().toString());
        response.put("balance", account.get().getBalance());
        return ResponseEntity.ok(response);
    }
} 