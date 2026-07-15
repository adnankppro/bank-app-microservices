package com.adnan.bankapp.otp.service;

import com.adnan.bankapp.otp.dto.OtpRequest;
import com.adnan.bankapp.otp.dto.ValidateOtpRequest;
import com.adnan.bankapp.otp.exception.OtpGenerateLimitExceededException;
import com.adnan.bankapp.otp.exception.OtpValidateLimitExceededException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public void generate(OtpRequest request) {
        
        final String otpKey = "otp:"+request.email()+":"+request.purpose();
        final String lastHourCountKey = "otp:count:lastHour"+request.email()+":"+request.purpose();
        final String recentCountKey = "otp:count:recent"+request.email()+":"+request.purpose();

        Long recentCount = redisTemplate.opsForValue().increment(recentCountKey);
        if(recentCount != null && recentCount>1){
            log.warn("Recent OTP limit exceeded for email: {} | purpose: {}", request.email(), request.purpose());
            Long expire = redisTemplate.getExpire(recentCountKey, TimeUnit.SECONDS);
            throw new OtpGenerateLimitExceededException("OTP Limit exceeded. Please try again after " + (expire>=0 ? expire : "few") + " seconds.");
        }
        if (recentCount != null && recentCount == 1) {
            redisTemplate.expire(recentCountKey, Duration.ofSeconds(120));
        }

        Long lastHourCount = redisTemplate.opsForValue().increment(lastHourCountKey);
        if (lastHourCount != null && lastHourCount>5){
            log.warn("Last hour OTP limit exceeded for email: {} | purpose: {}", request.email(), request.purpose());
            Long expire = redisTemplate.getExpire(lastHourCountKey, TimeUnit.MINUTES);
            throw new OtpGenerateLimitExceededException("OTP Limit exceeded. Please try again after " + (expire>=0 ? expire:"few") +" minutes.");
        }
        if(lastHourCount != null && lastHourCount == 1) {
            redisTemplate.expire(lastHourCountKey, Duration.ofMinutes(60));
        }

        final String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        redisTemplate.opsForValue().set(otpKey, passwordEncoder.encode(otp), Duration.ofSeconds(300));
        log.info("OTP generated successfully | email: {} | purpose: {}", request.email(), request.purpose());

        // send notification using feign (as OTP should not be async because it necessary to get before ttl expires)
    }


    public boolean validate(@Valid ValidateOtpRequest request) {
        log.info("Validating OTP | email: {} | purpose: {}", request.email(), request.purpose());

        final String otpKey = "otp:"+request.email()+":"+request.purpose();
        final String failedAttemptKey = "otp:failedAttempt"+request.email()+":"+request.purpose();
        
        String failedAttempt = redisTemplate.opsForValue().get(failedAttemptKey);
        if(failedAttempt!= null && Long.parseLong(failedAttempt) >= 5){
            redisTemplate.delete(otpKey);
            log.warn("OTP validation failed | reason: Too many attempts | email: {} | purpose: {}", request.email(), request.purpose());
            throw new OtpValidateLimitExceededException("Too many attempts");
        }

        String otp = redisTemplate.opsForValue().get(otpKey);
        if (otp == null || !passwordEncoder.matches(request.otp(), otp)){
            Long failedAttempts = redisTemplate.opsForValue().increment(failedAttemptKey);
            if (failedAttempts != null && failedAttempts == 1){
                redisTemplate.expire(failedAttemptKey, Duration.ofMinutes(30));
            }
            log.warn("OTP validation failed | reason: Invalid OTP | email: {} | purpose: {}", request.email(), request.purpose());
            return false;
        }

        final String recentCountKey = "otp:count:recent"+request.email()+":"+request.purpose();
        redisTemplate.delete(otpKey);
        redisTemplate.delete(recentCountKey);
        redisTemplate.delete(failedAttemptKey);

        log.info("OTP validated successfully | email: {} | purpose: {}", request.email(), request.purpose());

        return true;
    }
}
