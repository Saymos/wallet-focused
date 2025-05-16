package com.cubeia.wallet_focused.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionEntryTest {
    @Test
    void testTransactionEntryCreationAndAccessors() {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID counterpartyId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");
        TransactionEntry.Type type = TransactionEntry.Type.DEBIT;
        Instant timestamp = Instant.now();
        TransactionEntry entry = new TransactionEntry(transactionId, accountId, counterpartyId, amount, type, timestamp);
        assertEquals(transactionId, entry.getTransactionId());
        assertEquals(accountId, entry.getAccountId());
        assertEquals(counterpartyId, entry.getCounterpartyId());
        assertEquals(amount, entry.getAmount());
        assertEquals(type, entry.getType());
        assertEquals(timestamp, entry.getTimestamp());
    }

    @Test
    void testSetters() {
        TransactionEntry entry = new TransactionEntry(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ZERO, TransactionEntry.Type.CREDIT, Instant.now());
        BigDecimal newAmount = new BigDecimal("75.00");
        entry.setAmount(newAmount);
        assertEquals(newAmount, entry.getAmount());
    }
} 