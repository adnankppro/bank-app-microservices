package com.adnan.bankapp.profile.exception;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.HttpStatus;

public class ProfileNotFoundException extends BankAppException {
    public ProfileNotFoundException(String message) {
        super(message, HttpStatus.NO_CONTENT.value(), ErrorCode.PROFILE_NOT_FOUND);
    }
}
