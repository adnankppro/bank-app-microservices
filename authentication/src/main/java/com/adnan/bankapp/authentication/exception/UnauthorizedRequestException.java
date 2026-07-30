package com.adnan.bankapp.authentication.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class UnauthorizedRequestException extends BankAppException {
    public UnauthorizedRequestException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value(), ErrorCode.UNAUTHORIZED_REQUEST);
    }
}
