package com.bankapp.common.exception.handler;

import com.bankapp.common.dto.ErrorResponse;
import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.BankAppException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class BankAppGlobalExceptionHandler {

    @ExceptionHandler(BankAppException.class)
    public ResponseEntity<ErrorResponse> handleBankApp(BankAppException ex, HttpServletRequest request){
        return buildErrorResponse(ex.getStatusCode(), ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgument(MethodArgumentNotValidException ex, HttpServletRequest request){
        List<String> details = ex.getFieldErrors().stream()
                .map(err -> err.getField()+": "+err.getDefaultMessage())
                .toList();
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorCode.VALIDATION_ERROR, ex.getMessage(), request, details);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(),
                ErrorCode.METHOD_NOT_ALLOWED,
                ex.getMessage(),
                request,
                null);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ErrorCode.MALFORMED_JSON,
                "Request body is invalid",
                request,
                null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN.value(),
                ErrorCode.ACCESS_DENIED,
                ex.getMessage(),
                request,
                null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.AUTHENTICATION_ERROR,
                ex.getMessage(),
                request,
                null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>  handleGeneralException(Exception ex, HttpServletRequest request){
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), request, null);
    }


    private ResponseEntity<ErrorResponse> buildErrorResponse(
            int status, ErrorCode errorCode, String message,
            HttpServletRequest request, List<String> details) {

        ErrorResponse response = new ErrorResponse(message, errorCode, LocalDateTime.now(), status, request.getRequestURI(), details);

        return ResponseEntity.status(status).body(response);
    }

}
