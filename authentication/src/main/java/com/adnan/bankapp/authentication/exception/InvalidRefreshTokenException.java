package com.adnan.bankapp.authentication.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BankAppException {
    public InvalidRefreshTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
