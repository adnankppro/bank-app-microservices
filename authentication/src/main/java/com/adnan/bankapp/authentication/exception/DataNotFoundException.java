package com.adnan.bankapp.authentication.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class DataNotFoundException extends BankAppException {
    public DataNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), ErrorCode.DATA_NOT_FOUND);
    }
}
