package com.adnan.bankapp.banking.transaction.dto;

import com.adnan.bankapp.banking.transaction.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionFilter(BigDecimal amountGreaterThan,
                                BigDecimal amountLessThan,
                                TransactionType type,
                                String counterPartyAccountNumber) {
}
