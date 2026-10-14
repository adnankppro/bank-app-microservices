package com.adnan.bankapp.banking.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class IncorrectPinException extends BankAppException {
    public IncorrectPinException(String message) {
        super(message, HttpStatus.FORBIDDEN.value(), ErrorCode.INVALID_REQUEST);
    }
}
