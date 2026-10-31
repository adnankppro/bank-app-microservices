package com.adnan.bankapp.banking.util;

import com.adnan.bankapp.banking.account.enums.AccountStatus;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.service.AccountService;
import com.adnan.bankapp.banking.exception.IncorrectPinException;
import com.adnan.bankapp.banking.exception.InvalidAccountStatusException;
import com.adnan.bankapp.banking.exception.PinValidationLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonAccountUtil {

    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private static final long MAX_ALLOWED_FAILED_ATTEMPT = 5L;
    private static final String FAILED_ATTEMPT_KEY_PREFIX = "failed:pin:validation:attempt:";


    @Transactional(noRollbackFor = PinValidationLimitExceededException.class)
    public void validatePin(BankAccount bankAccount, String rawPin){
        log.info("Validating PIN | accountNumber: {}", bankAccount.getAccountNumber());

        if(bankAccount.getStatus() == AccountStatus.BLOCKED){
            log.warn("PIN validation failed | reason: account is blocked | accountNumber: {} ", bankAccount.getAccountNumber());
            throw new InvalidAccountStatusException("Your account is blocked. Please contact branch.");
        }

        String failedAttemptKey = FAILED_ATTEMPT_KEY_PREFIX+bankAccount.getAccountNumber();
        String failedAttemptValue = stringRedisTemplate.opsForValue().get(failedAttemptKey);

        long failedAttempt = 0;
        if (failedAttemptValue != null){
            failedAttempt = Long.parseLong(failedAttemptValue);
        }

        if(failedAttempt>=MAX_ALLOWED_FAILED_ATTEMPT){
            log.warn("Blocking account | reason: attempt limit reached | accountNumber: {} | attempt: {}", bankAccount.getAccountNumber(), failedAttempt);
            bankAccount.setStatus(AccountStatus.BLOCKED); // account will be blocked even if exception is thrown
            stringRedisTemplate.delete(failedAttemptKey); // deleting key after account block
            throw new PinValidationLimitExceededException("Your account is blocked due to too many failed attempts. Please contact branch");
        }

        if(!passwordEncoder.matches(rawPin, bankAccount.getPin())){
            log.warn("PIN validation failed | reason: incorrect PIN | accountNumber: {} | attempt: {}", bankAccount.getAccountNumber(), failedAttempt);
            failedAttempt++;
            if (failedAttempt >= MAX_ALLOWED_FAILED_ATTEMPT){
                log.warn("Blocking account | reason: attempt limit reached | accountNumber: {} | attempt: {}", bankAccount.getAccountNumber(), failedAttempt);
                bankAccount.setStatus(AccountStatus.BLOCKED); // account will be blocked even if exception is thrown
                stringRedisTemplate.delete(failedAttemptKey); // deleting key after account block
                throw new PinValidationLimitExceededException("Your account is blocked due to too many failed attempts. Please contact branch");
            } else if (failedAttempt==1) {
                stringRedisTemplate.opsForValue().set(failedAttemptKey, String.valueOf(1), Duration.ofHours(24));
            } else {
                stringRedisTemplate.opsForValue().increment(failedAttemptKey);
            }
            throw new IncorrectPinException("Wrong PIN. "+ (MAX_ALLOWED_FAILED_ATTEMPT - failedAttempt) +" attempt(s) left before blocking your account");
        }

        stringRedisTemplate.delete(failedAttemptKey); // deleting key after successful pin validation

        log.info("PIN validated successfully | accountNumber: {}", bankAccount.getAccountNumber());
    }

}
