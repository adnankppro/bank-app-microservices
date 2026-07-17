package com.adnan.bankapp.otp.controller;


import com.adnan.bankapp.otp.service.OtpService;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.dto.OtpValidateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void generate(@Valid @RequestBody OtpGenerateRequest request){
        otpService.generate(request);
    }

    @PostMapping("/validate")
    public void validate(@Valid @RequestBody OtpValidateRequest request){
        otpService.validate(request);
    }

}
