package com.adnan.bankapp.banking.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class InvalidAccountStatusException extends BankAppException {
    public InvalidAccountStatusException(String message) {
        super(message, HttpStatus.BAD_GATEWAY.value(), ErrorCode.INVALID_ACCOUNT_STATUS);
    }
}
