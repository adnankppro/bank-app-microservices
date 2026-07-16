package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;

public class OtpValidateLimitExceededException extends BankAppException{
    public OtpValidateLimitExceededException(String message) {
        super(message, 429, ErrorCode.OTP_VALIDATE_LIMIT_EXCEEDED);
    }
}
