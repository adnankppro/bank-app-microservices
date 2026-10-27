package com.adnan.bankapp.otp.exception.handler;

import com.bankapp.common.dto.ErrorResponse;
import com.bankapp.common.exception.BankAppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankAppException.class)
    public ResponseEntity<ErrorResponse> bankAppException(BankAppException e){
        return ResponseEntity.status(e.getStatusCode()).body(new ErrorResponse(e.getMessage(), e.getErrorCode(), LocalDateTime.now().toString()));
    }

}
