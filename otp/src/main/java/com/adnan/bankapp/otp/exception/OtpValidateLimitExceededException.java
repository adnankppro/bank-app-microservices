package com.adnan.bankapp.otp.exception;

public class OtpValidateLimitExceededException extends RuntimeException {
    public OtpValidateLimitExceededException(String message) {
        super(message);
    }
}
