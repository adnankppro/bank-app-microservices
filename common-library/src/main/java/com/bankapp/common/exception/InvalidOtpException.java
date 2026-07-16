package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;

public class InvalidOtpException extends BankAppException{
    public InvalidOtpException(String message) {
        super(message, 400, ErrorCode.INVALID_OTP);
    }
}
