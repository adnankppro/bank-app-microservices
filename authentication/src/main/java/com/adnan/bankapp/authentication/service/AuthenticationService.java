package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.dto.VerifyRegisterRequest;
import com.adnan.bankapp.authentication.entity.UserCredentials;
import com.adnan.bankapp.authentication.enums.Role;
import com.adnan.bankapp.authentication.enums.UserStatus;
import com.adnan.bankapp.authentication.exception.DataNotFoundException;
import com.adnan.bankapp.authentication.exception.EmailAlreadyExistsException;
import com.adnan.bankapp.authentication.feign.BranchFeignClient;
import com.adnan.bankapp.authentication.feign.OtpFeignClient;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.dto.OtpValidateRequest;
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

        branchFeignClient.exists(request.branchCode());

        String key = "register:"+request.email();
        redisTemplate.opsForValue().set(key, request, 5, TimeUnit.MINUTES);

        // create and send otp
        otpFeignClient.generate(new OtpGenerateRequest(request.email(), OtpPurpose.REGISTER));

        log.info("Registration initiated successfully | email: {}", request.email());
    }

    @Transactional
    public void verifyRegister(VerifyRegisterRequest request) {
        log.info("Register verification request received | email: {}", request.email());
        String key = "register:"+request.email();
        RegisterRequest registerRequest = redisTemplate.opsForValue().get(key);

        if (registerRequest == null){
            log.warn("Register verification request failed | reason: data not found in Redis | email: {}", request.email());
            throw new DataNotFoundException("Data not found! Please register again if you don't have an account");
        }

        if( repository.existsByEmail(request.email())){
            log.warn("Register verification request failed | reason: email already registered | email: {}", request.email());
            throw new EmailAlreadyExistsException("Email already registered.");
        }

        otpFeignClient.validate(new OtpValidateRequest(request.email(), OtpPurpose.REGISTER, request.otp()));

        UserCredentials user = new UserCredentials(request.email());
        user.setStatus(UserStatus.PROCESSING);
        user.setRole(Role.USER);
        repository.save(user);

        // create a new service user-profile and call it's register method to save user details as this only save authentication related data. Also in profile entity ID will not be auto generated, every data will be passed from here including ID, which is just generated (user.getId)

        redisTemplate.delete(key);

        log.info("Register verification completed successfully | email: {}", request.email());
    }
}
