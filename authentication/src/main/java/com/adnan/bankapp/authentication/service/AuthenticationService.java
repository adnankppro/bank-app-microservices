package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.dto.RegisterResponse;
import com.adnan.bankapp.authentication.entity.UserCredentials;
import com.adnan.bankapp.authentication.enums.Role;
import com.adnan.bankapp.authentication.enums.UserStatus;
import com.adnan.bankapp.authentication.exception.EmailAlreadyUserException;
import com.adnan.bankapp.authentication.repository.UserRepository;
import jakarta.validation.Valid;
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

    public void register(RegisterRequest request) {
        log.info("Checking email existence for: {}", request.email());
        if(repository.existsByEmail(request.email())){
            log.warn("Registration attempt with existing email: {}", request.email());
            throw new EmailAlreadyUserException("This is Email is already registered.");
        }
        // check branch exists to register
        String key = "register:"+request.email();
        redisTemplate.opsForValue().set(key, request);
        // create otp

        //logging unit test
    }
}
