package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BankAppException{
    public EmailAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value(), ErrorCode.EMAIL_ALREADY_EXISTS);
    }
}