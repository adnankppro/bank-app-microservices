package com.adnan.bankapp.authentication.feign.client;

import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.dto.OtpValidateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("OTP-SERVICE")
public interface OtpFeignClient {

    @PostMapping("/otp/generate")
    public void generate(@RequestBody OtpGenerateRequest request);

    @PostMapping("/otp/validate")
    public void validate(@RequestBody OtpValidateRequest request);

}
