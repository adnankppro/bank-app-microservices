package com.adnan.bankapp.otp.controller;

import com.adnan.bankapp.otp.dto.OtpRequest;
import com.adnan.bankapp.otp.dto.OtpResponse;
import com.adnan.bankapp.otp.dto.ValidateOtpRequest;
import com.adnan.bankapp.otp.service.OtpService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OTP", description = "APIs for OTP")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> generate(@Valid @RequestBody OtpRequest request){
        otpService.generate(request);
        return null;
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@Valid @RequestBody ValidateOtpRequest request){
        boolean valid = otpService.validate(request);
        return null;
    }

}
