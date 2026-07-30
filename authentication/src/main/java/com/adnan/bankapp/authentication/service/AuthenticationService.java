package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.dto.PasswordSetupRequest;
import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.dto.VerifyPasswordSetupRequest;
import com.adnan.bankapp.authentication.dto.VerifyRegisterRequest;
import com.adnan.bankapp.authentication.entity.UserCredentials;
import com.adnan.bankapp.authentication.enums.Role;
import com.adnan.bankapp.authentication.enums.UserStatus;
import com.adnan.bankapp.authentication.exception.DataNotFoundException;
import com.adnan.bankapp.authentication.exception.EmailAlreadyExistsException;
import com.adnan.bankapp.authentication.exception.IncorrectPasswordException;
import com.adnan.bankapp.authentication.exception.UnauthorizedRequestException;
import com.adnan.bankapp.authentication.feign.BranchFeignClient;
import com.adnan.bankapp.authentication.feign.OtpFeignClient;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.dto.OtpValidateRequest;
import com.bankapp.common.enums.OtpPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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

        // create a new service user-profile and call its register method to save user details as this only save authentication related data. Also in profile entity ID will not be auto generated, every data will be passed from here including ID, which is just generated (user.getId)

        redisTemplate.delete(key);

        log.info("Register verification completed successfully | email: {}", request.email());
    }

    @Transactional(readOnly = true)
    public void generatePasswordOtp(PasswordSetupRequest request) {
        log.info("Request for password generation OTP received | email: {}", request.email());

        if(!repository.existsByEmailAndStatus(request.email(), UserStatus.PENDING_PASSWORD_GENERATION)){
            log.warn("Request for password generation OTP failed | reason: unauthorized request | email: {}", request.email());
            throw new UnauthorizedRequestException("Invalid request");
        }

        otpFeignClient.generate(new OtpGenerateRequest(request.email(), OtpPurpose.GENERATE_PASSWORD));

        log.info("Password generation OTP created successfully | email: {}", request.email());
    }

    @Transactional
    public void setPassword(VerifyPasswordSetupRequest request) {
        log.info("Request for password setup received | email: {}", request.email());

        if(!request.password().equals(request.confirmPassword())){
            log.warn("Password setup failed | reason: passwords do not match | email: {}", request.email());
            throw new IncorrectPasswordException("Passwords do not match");
        }

        UserCredentials user = repository.getByEmailAndStatus(request.email(), UserStatus.PENDING_PASSWORD_GENERATION).orElseThrow(() -> {
            log.warn("Password setup failed | reason: invalid request | email: {}", request.email());
            return new UnauthorizedRequestException("Invalid request");
        });

        otpFeignClient.validate(new OtpValidateRequest(request.email(), OtpPurpose.GENERATE_PASSWORD, request.otp()));

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);

        log.info("Password setup completed successfully | email: {}", request.email());
    }
}
