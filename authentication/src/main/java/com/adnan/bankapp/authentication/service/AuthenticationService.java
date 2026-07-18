package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.exception.EmailAlreadyUserException;
import com.adnan.bankapp.authentication.feign.OtpServiceClient;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.enums.OtpPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableFeignClients
public class AuthenticationService {

    private final RedisTemplate<String, RegisterRequest> redisTemplate;
    private final UserRepository repository;
    private final OtpServiceClient otpServiceClient;

    public void register(RegisterRequest request) {
        log.info("Checking email existence for: {}", request.email());
        if(repository.existsByEmail(request.email())){
            log.warn("Registration attempt with existing email: {}", request.email());
            throw new EmailAlreadyUserException("This is Email is already registered.");
        }
        // check branch exists to register
        String key = "register:"+request.email();

        // create otp
        otpServiceClient.generate(new OtpGenerateRequest(request.email(), OtpPurpose.REGISTER));
        
        redisTemplate.opsForValue().set(key, request);


        //logging unit test
    }
}
