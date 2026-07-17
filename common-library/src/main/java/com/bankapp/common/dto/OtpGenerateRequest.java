package com.bankapp.common.dto;

import com.bankapp.common.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpGenerateRequest(@NotBlank String email,
                                 @NotNull OtpPurpose otpPurpose) {
}
