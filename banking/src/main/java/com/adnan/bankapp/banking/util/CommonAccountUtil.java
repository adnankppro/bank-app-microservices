package com.adnan.bankapp.banking.util;

import com.adnan.bankapp.banking.account.AccountStatus;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.exception.IncorrectPinException;
import com.adnan.bankapp.banking.account.exception.InvalidAccountNumberException;
import com.adnan.bankapp.banking.account.exception.PinValidationLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonAccountUtil {


    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;

    public void validatePin(BankAccount bankAccount, String rawPin){
        log.info("Validating PIN | accountNumber: {}", bankAccount.getAccountNumber());

        String pinValidationAttemptKey = "pin:validation:attempt:"+bankAccount.getAccountNumber();
        String maxContinuesFailedAttemptKey = "max:continues:failed:attempt:"+bankAccount.getAccountNumber();
        Long maxContinuesFailedAttempt = stringRedisTemplate.opsForValue().increment(maxContinuesFailedAttemptKey);
        Long pinValidationAttempt = stringRedisTemplate.opsForValue().increment(pinValidationAttemptKey);

        int MAXIMUM_CONTINUES_FAILED_ATTEMPT = 6;
        if (maxContinuesFailedAttempt > MAXIMUM_CONTINUES_FAILED_ATTEMPT){
            log.warn("PIN validation failed | reason: attempt limit reached | accountNumber: {} | attempt: {}", bankAccount.getAccountNumber(), pinValidationAttempt);
            if(bankAccount.getStatus() != AccountStatus.BLOCKED){
                log.info("Request received to block account | accountNumber: {}", bankAccount.getAccountNumber());
                bankAccount.setStatus(AccountStatus.BLOCKED);
                log.info("Account blocked successfully | accountNumber: {}", bankAccount.getAccountNumber());

            }
            stringRedisTemplate.delete(maxContinuesFailedAttemptKey);
            throw new PinValidationLimitExceededException("Too many failed attempts. Please contact branch");
        }

        int MAXIMUM_PIN_VALIDATION_ATTEMPT = 3;
        if (pinValidationAttempt > MAXIMUM_PIN_VALIDATION_ATTEMPT){
            log.warn("PIN validation failed | reason: attempt limit reached | accountNumber: {} | attempt: {}", bankAccount.getAccountNumber(), pinValidationAttempt);
            throw new PinValidationLimitExceededException("PIN validation limit exceeded. Please try again after "+ stringRedisTemplate.getExpire(pinValidationAttemptKey, TimeUnit.MINUTES)+" minutes.");
        }

        if (pinValidationAttempt == 1){
            stringRedisTemplate.expire(pinValidationAttemptKey, Duration.ofMinutes(60));
        }

        if (maxContinuesFailedAttempt == 1){
            stringRedisTemplate.expire(maxContinuesFailedAttemptKey, Duration.ofHours(24));
        }

        if(!passwordEncoder.matches(rawPin, bankAccount.getPin())){
            log.warn("PIN validation failed | reason: incorrect PIN | accountNumber: {} | attempt: {}", bankAccount.getAccountNumber(), pinValidationAttempt);
            throw new IncorrectPinException("Incorrect PIN.");
        }

        stringRedisTemplate.delete(pinValidationAttemptKey);
        stringRedisTemplate.delete(maxContinuesFailedAttemptKey);

        log.info("PIN validated successfully | accountNumber; {}", bankAccount.getAccountNumber());
    }

}
