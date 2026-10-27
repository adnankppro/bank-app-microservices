package com.adnan.bankapp.banking.transaction.dto;

import com.adnan.bankapp.banking.transaction.enums.TransactionPurpose;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionRequest(@NotBlank(message = "Receiver account number must not be null")
                                 @Pattern(regexp = "^\\d{10}$", message = "Receiver account number must be 10 digit")
                                 String receiver,

                                 @NotBlank(message = "Sender account number must not be null")
                                 @Pattern(regexp = "^\\d{10}$", message = "Sender account number must be 10 digit")
                                 String sender,

                                 @NotBlank(message = "PIN must not be null")
                                 @Pattern(regexp = "^\\d{4}$", message = "PIN must be 4 digit")
                                 String pin,

                                 @NotNull(message = "Amount must not be null")
                                 @Min(value = 1, message = "Minimum transaction amount is 1 rupees")
                                 @Max(value = 100000, message = "Maximum amount per transaction is 100000 rupees")
                                 BigDecimal amount,

                                 @NotNull(message = "Please select transaction purpose")
                                 TransactionPurpose purpose
                                 ) {
}
