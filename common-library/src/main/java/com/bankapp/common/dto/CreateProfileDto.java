package com.bankapp.common.dto;

import com.bankapp.common.enums.UserStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record CreateProfileDto(@NotNull @Min(1) Long id,
                               @NotBlank String name,
                               @NotBlank String email,
                               @NotBlank String address,
                               @NotNull Date dateOfBirth,
                               @NotNull String branch,
                               @NotNull UserStatus status) {
}
