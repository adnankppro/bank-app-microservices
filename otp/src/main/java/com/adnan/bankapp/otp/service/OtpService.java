package com.adnan.bankapp.otp.service;



import com.adnan.bankapp.otp.producer.OtpEventProducer;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.dto.OtpValidateRequest;
import com.bankapp.common.event.OtpGeneratedEvent;
import com.bankapp.common.exception.InvalidOtpException;
import com.bankapp.common.exception.OtpGenerateLimitExceededException;
import com.bankapp.common.exception.OtpValidateLimitExceededException;
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
    private final OtpEventProducer otpEventProducer;

    public void generate(OtpGenerateRequest request) {
        
        final String otpKey = "otp:"+request.email()+":"+request.otpPurpose();
        final String lastHourCountKey = "otp:count:lastHour"+request.email()+":"+request.otpPurpose();
        final String recentCountKey = "otp:count:recent"+request.email()+":"+request.otpPurpose();

        Long recentCount = redisTemplate.opsForValue().increment(recentCountKey);
        if(recentCount != null && recentCount>1){
            log.warn("Recent OTP limit exceeded for email: {} | purpose: {}", request.email(), request.otpPurpose());
            Long expire = redisTemplate.getExpire(recentCountKey, TimeUnit.SECONDS);
            throw new OtpGenerateLimitExceededException("OTP Limit exceeded. Please try again after " + (expire>=0 ? expire : "few") + " seconds.");
        }
        if (recentCount != null && recentCount == 1) {
            redisTemplate.expire(recentCountKey, Duration.ofSeconds(120));
        }

        Long lastHourCount = redisTemplate.opsForValue().increment(lastHourCountKey);
        if (lastHourCount != null && lastHourCount>5){
            log.warn("Last hour OTP limit exceeded for email: {} | purpose: {}", request.email(), request.otpPurpose());
            Long expire = redisTemplate.getExpire(lastHourCountKey, TimeUnit.MINUTES);
            throw new OtpGenerateLimitExceededException("OTP Limit exceeded. Please try again after " + (expire>=0 ? expire:"few") +" minutes.");
        }
        if(lastHourCount != null && lastHourCount == 1) {
            redisTemplate.expire(lastHourCountKey, Duration.ofMinutes(60));
        }

        final String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        redisTemplate.opsForValue().set(otpKey, passwordEncoder.encode(otp), Duration.ofSeconds(300));
        log.info("OTP generated successfully | email: {} | purpose: {}", request.email(), request.otpPurpose());

        otpEventProducer.publish(new OtpGeneratedEvent(request.email(), otp, request.otpPurpose()));
        //NO (im using Kafka now) send notification using feign (as OTP should not be async because it necessary to get before ttl expires)
    }


    public void validate(OtpValidateRequest request) {
        log.info("Validating OTP | email: {} | purpose: {}", request.email(), request.otpPurpose());

        final String otpKey = "otp:"+request.email()+":"+request.otpPurpose();
        final String failedAttemptKey = "otp:failedAttempt"+request.email()+":"+request.otpPurpose();
        
        String failedAttempt = redisTemplate.opsForValue().get(failedAttemptKey);
        if(failedAttempt!= null && Long.parseLong(failedAttempt) >= 5){
            redisTemplate.delete(otpKey);
            log.warn("OTP validation failed | reason: Too many attempts | email: {} | purpose: {}", request.email(), request.otpPurpose());
            throw new OtpValidateLimitExceededException("Too many attempts. Please try again later.");
        }

        String otp = redisTemplate.opsForValue().get(otpKey);
        if (otp == null || !passwordEncoder.matches(request.otp(), otp)){
            Long failedAttempts = redisTemplate.opsForValue().increment(failedAttemptKey);
            if (failedAttempts != null && failedAttempts == 1){
                redisTemplate.expire(failedAttemptKey, Duration.ofMinutes(30));
            }
            log.warn("OTP validation failed | reason: Invalid OTP | email: {} | purpose: {}", request.email(), request.otpPurpose());
            throw new InvalidOtpException("Invalid OTP");
        }

        final String recentCountKey = "otp:count:recent"+request.email()+":"+request.otpPurpose();
        redisTemplate.delete(otpKey);
        redisTemplate.delete(recentCountKey);
        redisTemplate.delete(failedAttemptKey);

        log.info("OTP validated successfully | email: {} | purpose: {}", request.email(), request.otpPurpose());
    }
}
