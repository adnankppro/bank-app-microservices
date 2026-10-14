package com.adnan.bankapp.banking.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositResponse(String accountNumber,
                              BigDecimal amount,
                              LocalDateTime time
                              ) {
}
