package com.adnan.bankapp.banking.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountTypeRequest(
        @NotBlank(message = "accountTypeName must not be null.")
        String accountTypeName,
        @NotNull(message = "dailySendTransactionAmountLimit must not be null.")
        BigDecimal dailySendTransactionAmountLimit,
        @NotNull(message = "dailySendTransactionCountLimit must not be null.")
        Integer dailySendTransactionCountLimit,
        @NotNull(message = "dailyReceiveTransactionAmountLimit must not be null.")
        BigDecimal dailyReceiveTransactionAmountLimit,
        @NotNull(message = "dailyReceiveTransactionCountLimit must not be null.")
        Integer dailyReceiveTransactionCountLimit,
        @NotNull(message = "maxSendTransactionAmount must not be null.")
        BigDecimal maxSendTransactionAmount
) {
}
