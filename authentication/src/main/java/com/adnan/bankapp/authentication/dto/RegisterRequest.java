package com.adnan.bankapp.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String address,
        @NotNull Date dateOfBirth,
        @NotNull Long homeBranch
        ) {
}
