package com.adnan.bankapp.banking.account.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class DuplicateAccountNumberException extends BankAppException {
    public DuplicateAccountNumberException(String message) {
        super(message, HttpStatus.GATEWAY_TIMEOUT.value(), ErrorCode.SERVICE_ERROR);
    }
}
