package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserIdNotFoundException extends BankAppException {
    public UserIdNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), ErrorCode.ID_NOT_FOUND);
    }
}
