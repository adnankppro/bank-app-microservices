package com.adnan.bankapp.banking.transaction.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DepositRequest(@NotBlank(message = "Receiver account number must not be null")
                             @Pattern(regexp = "^\\d{10}$", message = "Receiver account number must be 10 digit")
                             String accountNumber,
                             @NotNull(message = "Amount must not be null")
                             @Min(value = 100, message = "Minimum transaction amount is 100 rupees")
                             @Max(value = 500000, message = "Maximum amount per deposit is 500000 rupees")
                             BigDecimal amount) {
}
