package com.adnan.bankapp.banking.transaction.dto;


import com.adnan.bankapp.banking.transaction.enums.TransactionPurpose;
import com.adnan.bankapp.banking.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(TransactionType transactionType,
                                  String name,
                                  String accountNumber,
                                  BigDecimal amount,
                                  LocalDateTime time,
                                  TransactionPurpose transactionPurpose
                                  ) {
}
