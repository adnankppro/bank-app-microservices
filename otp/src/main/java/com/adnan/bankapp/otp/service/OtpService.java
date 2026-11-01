package com.adnan.bankapp.otp.service;



import com.adnan.bankapp.otp.producer.OtpEventProducer;
import com.bankapp.common.dto.OtpGenerateRequest;
import com.bankapp.common.dto.OtpValidateRequest;
import com.bankapp.common.event.OtpGeneratedEvent;
import com.bankapp.common.exception.InvalidOtpException;
import com.bankapp.common.exception.OtpGenerateLimitExceededException;
import com.bankapp.common.exception.OtpValidateLimitExceededException;
import com.bankapp.common.exception.ServiceException;
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
        
        String otpKey = "otp:"+request.email()+":"+request.otpPurpose();
        String lastHourCountKey = "otp:count:lastHour:"+request.email()+":"+request.otpPurpose();
        String recentGenerationKey = "otp:count:recent:"+request.email()+":"+request.otpPurpose();

        Boolean generatedRecently = redisTemplate.opsForValue().setIfAbsent(recentGenerationKey, String.valueOf(1), Duration.ofSeconds(120));
        if (generatedRecently == null){
            log.error("OTP generation failed | reason: can't verify whether otp is recently generated or not | email: {}", request.email());
            throw new ServiceException("OTP generation failed. Please try again later.");
        }
        if(generatedRecently.equals(Boolean.FALSE)){
            log.warn("Recent OTP limit exceeded for email: {} | purpose: {}", request.email(), request.otpPurpose());
            Long expire = redisTemplate.getExpire(recentGenerationKey, TimeUnit.SECONDS);
            throw new OtpGenerateLimitExceededException("OTP Limit exceeded. Please try again after " + (expire>0 ? expire : "few") + " seconds.");
        }

        Long lastHourCount = redisTemplate.opsForValue().increment(lastHourCountKey);

        long lastHourCountExpireTime = redisTemplate.getExpire(lastHourCountKey, TimeUnit.MINUTES);
        if (lastHourCountExpireTime == -1){
            redisTemplate.expire(lastHourCountKey, Duration.ofMinutes(60));
        }

        if (lastHourCount == null){
            log.error("OTP generation failed | reason: can't verify last hour count | email: {}", request.email());
            redisTemplate.delete(recentGenerationKey);// as otp not generated, deleting recentlyGenerated
            throw new ServiceException("OTP generation failed. Please try again later.");
        }

        if (lastHourCount > 5){
            log.warn("Last hour OTP limit exceeded for email: {} | purpose: {}", request.email(), request.otpPurpose());
            throw new OtpGenerateLimitExceededException("OTP Limit exceeded. Please try again after " + (lastHourCountExpireTime >=1 ? lastHourCountExpireTime:"few") +" minutes.");
        }

        final String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        redisTemplate.opsForValue().set(otpKey, passwordEncoder.encode(otp), Duration.ofSeconds(300));
        log.info("OTP generated successfully | email: {} | purpose: {}", request.email(), request.otpPurpose());

        otpEventProducer.publish(new OtpGeneratedEvent(request.email(), otp, request.otpPurpose()));
   }


    public void validate(OtpValidateRequest request) {
        log.info("Validating OTP | email: {} | purpose: {}", request.email(), request.otpPurpose());

        String otpKey = "otp:"+request.email()+":"+request.otpPurpose();
        String failedAttemptKey = "otp:failedAttempt"+request.email()+":"+request.otpPurpose();

        Long failedAttempt = redisTemplate.opsForValue().increment(failedAttemptKey);
        Long attemptCountTTL = redisTemplate.getExpire(failedAttemptKey);// will return -1 if no TTL
        if (attemptCountTTL == -1){ // if TTL is not set
            redisTemplate.expire(failedAttemptKey, Duration.ofMinutes(60));
        }

        if (failedAttempt == null){
            log.error("OTP validation failed | reason: couldn't verify failed attempt count| email: {} | purpose: {}", request.email(), request.otpPurpose());
            throw new ServiceException("OTP validation failed. Please try again later.");
        }

        if(failedAttempt > 5){
            log.warn("OTP validation failed | reason: Too many attempts | email: {} | purpose: {}", request.email(), request.otpPurpose());
            redisTemplate.delete(otpKey);
            throw new OtpValidateLimitExceededException("Too many attempts. Please try again later.");
        }

        String otp = redisTemplate.opsForValue().get(otpKey);
        if (otp == null){
            log.warn("OTP validation failed | reason: Invalid OTP | email: {} | purpose: {}", request.email(), request.otpPurpose());
            throw new InvalidOtpException("Invalid OTP");
        }

        if (!passwordEncoder.matches(request.otp(), otp)){
            log.warn("OTP validation failed | reason: Invalid OTP | email: {} | purpose: {}", request.email(), request.otpPurpose());
            throw new InvalidOtpException("Invalid OTP");
        }

        final String recentCountKey = "otp:count:recent"+request.email()+":"+request.otpPurpose();
        redisTemplate.delete(failedAttemptKey);
        redisTemplate.delete(recentCountKey);
        redisTemplate.delete(otpKey);

        log.info("OTP validated successfully | email: {} | purpose: {}", request.email(), request.otpPurpose());
    }
}
