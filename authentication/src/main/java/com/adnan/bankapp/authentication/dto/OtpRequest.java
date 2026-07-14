package com.adnan.bankapp.authentication.dto;

import com.adnan.bankapp.authentication.enums.OtpPurpose;

public record OtpRequest(
        String email,
        OtpPurpose purpose
) {
}
