package com.adnan.bankapp.banking.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PinGenerationRequest(@NotBlank(message = "Account number must not be null") @Pattern(regexp = "^\\d{10}$", message = "Account number must be exactly 10 digits") String accountNumber,
                                   @NotBlank(message = "PIN must not be null") @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits") String pin,
                                   @NotBlank(message = "PIN must not be null") @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits") String confirmPin) {
}
