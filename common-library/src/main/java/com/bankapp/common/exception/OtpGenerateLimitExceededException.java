package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;

public class OtpGenerateLimitExceededException extends BankAppException{
    public OtpGenerateLimitExceededException(String message) {
        super(message, 429, ErrorCode.OTP_GENERATE_LIMIT_EXCEEDED);
    }
}
