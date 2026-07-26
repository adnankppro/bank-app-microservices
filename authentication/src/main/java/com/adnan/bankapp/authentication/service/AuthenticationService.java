package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.exception.EmailAlreadyExistsException;
import com.adnan.bankapp.authentication.feign.BranchFeignClient;
import com.adnan.bankapp.authentication.feign.OtpFeignClient;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.enums.OtpPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final RedisTemplate<String, RegisterRequest> redisTemplate;
    private final UserRepository repository;
    private final OtpFeignClient otpFeignClient;
    private final BranchFeignClient branchFeignClient;

    @Transactional(readOnly = true)
    public void register(RegisterRequest request) {
        log.info("Register request received | email: {}", request.email());

        if(repository.existsByEmail(request.email())){
            log.warn("Registration failed | reason: existing email | email: {}", request.email());
            throw new EmailAlreadyExistsException("Email is already registered.");
        }

        // check branch exists to register
        branchFeignClient.exists(request.branchCode());

        String key = "register:"+request.email();
        redisTemplate.opsForValue().set(key, request, 5, TimeUnit.MINUTES);

        // create otp
        otpFeignClient.generate(new OtpGenerateRequest(request.email(), OtpPurpose.REGISTER));

        log.info("Registration initiated successfully | email: {}", request.email());
    }
}
