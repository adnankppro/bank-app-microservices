package com.adnan.bankapp.otp.exception;

public class OtpGenerateLimitExceededException extends RuntimeException {
    public OtpGenerateLimitExceededException(String message) {
        super(message);
    }
}
