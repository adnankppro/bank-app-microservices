package com.adnan.bankapp.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyPasswordSetupRequest(@NotBlank @Email String email,
                                         @NotBlank @Size(min = 8) String password,
                                         @NotBlank @Size(min = 8) String confirmPassword,
                                         @NotBlank String otp) {
}
