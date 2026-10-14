package com.bankapp.common.dto;


public record CreateBankAccountRequest(Long userId,
                                       String accountHolderName,
                                       Long accountTypeId,
                                       String branchCode) {
}
