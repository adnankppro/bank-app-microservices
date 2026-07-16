package com.adnan.bankapp.authentication.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean status,
        String message,
        T data,
        LocalDateTime timestamp
) {
}
