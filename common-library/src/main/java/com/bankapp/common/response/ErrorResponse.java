package com.bankapp.common.response;

import com.bankapp.common.enums.ErrorCode;

import java.time.LocalDateTime;

public record ErrorResponse(String message,
                            ErrorCode errorCode,
                            LocalDateTime timestamp) {
}
