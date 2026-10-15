package com.bankapp.common.event;

public record CreateBankAccountEvent(Long userId,
                                     String accountHolderName,
                                     Long accountTypeId,
                                     String branchCode) {
}
