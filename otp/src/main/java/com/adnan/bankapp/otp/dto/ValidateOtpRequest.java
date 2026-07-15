package com.adnan.bankapp.otp.dto;

import com.adnan.bankapp.otp.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidateOtpRequest(@NotBlank String email,
                                 @NotBlank String otp,
                                 @NotNull OtpPurpose purpose) {
}
