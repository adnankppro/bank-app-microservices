package com.adnan.bankapp.banking.account.dto;

import java.math.BigDecimal;

public record AccountTypeDetails(
    String accountTypeName,
    BigDecimal dailySendTransactionAmountLimit,
    Integer dailySendTransactionCountLimit,
    BigDecimal dailyReceiveTransactionAmountLimit,
    Integer dailyReceiveTransactionCountLimit,
    BigDecimal maxSendTransactionAmount
) {
}
