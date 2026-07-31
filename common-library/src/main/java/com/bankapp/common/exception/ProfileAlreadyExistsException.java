package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ProfileAlreadyExistsException extends BankAppException {
    public ProfileAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value(), ErrorCode.PROFILE_ALREADY_EXISTS);
    }
}
