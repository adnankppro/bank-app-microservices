package com.adnan.bankapp.banking.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class TransactionLimitExceededException extends BankAppException {
    public TransactionLimitExceededException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_REQUEST);
    }
}
