package com.cubeia.wallet_focused.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cubeia.wallet_focused.config.GlobalExceptionHandler.ErrorResponse;
import com.cubeia.wallet_focused.config.GlobalExceptionHandler.ValidationErrorResponse;
import com.cubeia.wallet_focused.model.Account;
import com.cubeia.wallet_focused.model.TransactionEntry;
import com.cubeia.wallet_focused.model.TransferRequest;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet Service API")
                        .description("Double-entry bookkeeping wallet service for managing account balances and transfers")
                        .version("v1.0"))
                .addServersItem(new Server()
                        .url("/")
                        .description("Default Server URL"))
                .components(new Components()
                        .addSchemas("Account", new Schema<Account>().$ref(Account.class.getSimpleName()))
                        .addSchemas("TransactionEntry", new Schema<TransactionEntry>().$ref(TransactionEntry.class.getSimpleName()))
                        .addSchemas("TransferRequest", new Schema<TransferRequest>().$ref(TransferRequest.class.getSimpleName()))
                        .addSchemas("ErrorResponse", new Schema<ErrorResponse>().$ref(ErrorResponse.class.getSimpleName()))
                        .addSchemas("ValidationErrorResponse", new Schema<ValidationErrorResponse>().$ref(ValidationErrorResponse.class.getSimpleName()))
                );
    }
} 