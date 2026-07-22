package com.adnan.bankapp.branch.exception;

public class DuplicateBranchCodeException extends RuntimeException {
    public DuplicateBranchCodeException(String message) {
        super(message);
    }
}
