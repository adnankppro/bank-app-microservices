package com.bankapp.common.event;

import com.bankapp.common.enums.OtpPurpose;

public record OtpGeneratedEvent(String email, String otp, OtpPurpose otpPurpose) {
}
