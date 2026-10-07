package com.adnan.bankapp.banking.account.dto;

import java.math.BigDecimal;

public record AccountDetailsResponse(String accountNumber,
                                     String branchCode,
                                     Long userId,
                                     BigDecimal balance) {
}
