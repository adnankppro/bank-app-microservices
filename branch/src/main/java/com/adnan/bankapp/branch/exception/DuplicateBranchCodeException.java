package com.adnan.bankapp.branch.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class DuplicateBranchCodeException extends BankAppException {
    public DuplicateBranchCodeException(String message) {
        super(message, HttpStatus.CONFLICT.value(), ErrorCode.DUPLICATE_BRANCH_CODE);
    }
}
