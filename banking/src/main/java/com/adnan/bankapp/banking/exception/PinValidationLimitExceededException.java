package com.adnan.bankapp.banking.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class PinValidationLimitExceededException extends BankAppException {
    public PinValidationLimitExceededException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value(), ErrorCode.INCORRECT_PASSWORD);
    }
}
