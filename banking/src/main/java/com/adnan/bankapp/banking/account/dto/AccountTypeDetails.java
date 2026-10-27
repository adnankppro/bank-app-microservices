package com.adnan.bankapp.banking.account.dto;

import java.math.BigDecimal;

public record AccountTypeDetails(
    String accountTypeName,
    BigDecimal dailySendTransactionAmountLimit,
    Long dailySendTransactionCountLimit,
    BigDecimal dailyReceiveTransactionAmountLimit,
    Long dailyReceiveTransactionCountLimit,
    BigDecimal maxSendTransactionAmount
) {
}
