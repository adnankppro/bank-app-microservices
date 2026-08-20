package com.adnan.bankapp.authentication.client;

import com.adnan.bankapp.authentication.feign.OtpFeignClient;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.dto.OtpValidateRequest;
import com.bankapp.common.exception.ServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpServiceClient {

    private final OtpFeignClient otpFeignClient;

    @Retry(name = "default", fallbackMethod = "generateFallback")
    @CircuitBreaker(name = "default", fallbackMethod = "generateFallback")
    public void generate(OtpGenerateRequest request){
        otpFeignClient.generate(request);
    }

    @Retry(name = "default", fallbackMethod = "validateFallback")
    @CircuitBreaker(name = "default", fallbackMethod = "validateFallback")
    public void validate(OtpValidateRequest request){
        otpFeignClient.validate(request);
    }

    public void generateFallback(OtpGenerateRequest request, Exception e){
        log.error("OTP service unavailable | email: {} | error: {}", request.email(), e.getMessage());
        throw new ServiceException("OTP service temporarily unavailable.");
    }

    public void validateFallback(OtpValidateRequest request, Exception e){
        log.error("OTP service unavailable | email: {} | error: {}", request.email(), e.getMessage());
        throw new ServiceException("OTP service temporarily unavailable.");
    }
}
