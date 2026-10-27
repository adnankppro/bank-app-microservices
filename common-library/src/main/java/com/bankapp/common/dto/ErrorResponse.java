package com.bankapp.common.dto;

import com.bankapp.common.enums.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(String message,
                            ErrorCode errorCode,
                            LocalDateTime timestamp,
                            int status,
                            String path,
                            List<String> details) {
}
