package com.adnan.bankapp.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(@NotBlank String oldPassword,
                                    @NotBlank String newPassword,
                                    @NotBlank String confirmPassword) {
}
