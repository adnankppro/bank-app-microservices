package com.bankapp.common.dto;

import com.bankapp.common.enums.ErrorCode;

import java.time.LocalDateTime;

public record ErrorResponse(String message,
                            ErrorCode errorCode,
                            String timestamp) {
}
