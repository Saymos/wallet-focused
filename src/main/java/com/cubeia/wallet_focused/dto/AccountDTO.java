package com.cubeia.wallet_focused.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Valid
@Schema(description = "Account information")
public record AccountDTO(
    @NotNull(message = "Account ID is required")
    @Schema(description = "Unique identifier for the account", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID accountId,
    
    @NotNull(message = "Balance is required")
    @Schema(description = "Current balance of the account", example = "1000.00")
    BigDecimal balance
) {} 