package com.bankapp.common.exception;

import com.bankapp.common.enums.ErrorCode;

public class ServiceException extends BankAppException{
    public ServiceException(String message) {
        super(message, 500, ErrorCode.SERVICE_ERROR);
    }
}
