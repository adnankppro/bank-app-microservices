package com.adnan.bankapp.authentication.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class UserIdNotFoundException extends BankAppException {
    public UserIdNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), ErrorCode.ID_NOT_FOUND);
    }
}
