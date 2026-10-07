package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BankAppException {
    public InvalidRequestException(String message) {
        super(message, HttpStatus.FORBIDDEN.value(), ErrorCode.INVALID_REQUEST);
    }
}
