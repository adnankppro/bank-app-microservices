package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BankAppException extends RuntimeException {

    private final int statusCode;
    private final ErrorCode errorCode;

    public BankAppException(String message, int statusCode, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

}
