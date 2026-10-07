package com.bankapp.common.dto;

import com.bankapp.common.enums.BankAccountType;

public record CreateBankAccountRequest(Long userId,
                                       BankAccountType bankAccountType,
                                       String branchCode) {
}
