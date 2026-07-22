package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class BranchNotFoundException extends BankAppException{
    public BranchNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), ErrorCode.BRANCH_NOT_FOUND);
    }
}
