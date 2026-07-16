package com.adnan.bankapp.authentication.exception;

public class EmailAlreadyUserException extends RuntimeException {
    public EmailAlreadyUserException(String message) {
        super(message);
    }
}
