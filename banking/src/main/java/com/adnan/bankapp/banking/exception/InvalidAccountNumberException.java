package com.adnan.bankapp.banking.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class InvalidAccountNumberException extends BankAppException {
    public InvalidAccountNumberException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), ErrorCode.DATA_NOT_FOUND);
    }
}
