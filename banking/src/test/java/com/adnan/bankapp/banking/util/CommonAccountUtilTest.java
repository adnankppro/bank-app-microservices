package com.adnan.bankapp.banking.util;

import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.enums.AccountStatus;
import com.adnan.bankapp.banking.exception.IncorrectPinException;
import com.adnan.bankapp.banking.exception.InvalidAccountStatusException;
import com.adnan.bankapp.banking.exception.PinValidationLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommonAccountUtilTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CommonAccountUtil commonAccountUtil;

    private static final String FAILED_ATTEMPT_KEY_PREFIX = "failed:pin:validation:attempt:";

    @Nested
    public class validatePinTest{

        BankAccount bankAccount = new BankAccount();

        @BeforeEach
        public void setUp(){
            bankAccount.setAccountNumber("1122334455");
            bankAccount.setId(1L);
            bankAccount.setPin("1234");
        }

        @Test
        public void shouldThrowException_whenAccountIsBlocked(){
            bankAccount.setStatus(AccountStatus.BLOCKED);
            String rawPin = "4321";
            assertThrows(InvalidAccountStatusException.class, () -> commonAccountUtil.validatePin(bankAccount, rawPin));
            assertEquals(AccountStatus.BLOCKED, bankAccount.getStatus());
        }

        @ParameterizedTest
        @ValueSource(strings = {"5", "6"})
        public void shouldThrowExceptionAndBlockAccount_whenAttemptLimitAlreadyReached(String failedAttemptValue){
            bankAccount.setStatus(AccountStatus.ACTIVE);
            String rawPin = "4321";
            String key = FAILED_ATTEMPT_KEY_PREFIX+bankAccount.getAccountNumber();

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(stringRedisTemplate.opsForValue().get(key)).thenReturn(failedAttemptValue);

            assertThrows(PinValidationLimitExceededException.class, () -> commonAccountUtil.validatePin(bankAccount, rawPin));
            assertEquals(AccountStatus.BLOCKED, bankAccount.getStatus());
            verify(stringRedisTemplate, times(1)).delete(key);
        }

        @Test
        public void shouldThrowExceptionAndBlockAccount_whenAttemptLimitReach(){
            bankAccount.setStatus(AccountStatus.ACTIVE);
            String rawPin = "4321";
            String key = FAILED_ATTEMPT_KEY_PREFIX+bankAccount.getAccountNumber();

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(stringRedisTemplate.opsForValue().get(key)).thenReturn("4");
            when(passwordEncoder.matches(rawPin, bankAccount.getPin())).thenReturn(false);

            assertThrows(PinValidationLimitExceededException.class, () -> commonAccountUtil.validatePin(bankAccount, rawPin));
            assertEquals(AccountStatus.BLOCKED, bankAccount.getStatus());
            verify(stringRedisTemplate, times(1)).delete(key);
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "2", "3"})
        public void shouldThrowExceptionAndIncrementValueInRedis_whenPinIsIncorrect(String failedAttemptValue){
            bankAccount.setStatus(AccountStatus.ACTIVE);
            String rawPin = "4321";
            String key = FAILED_ATTEMPT_KEY_PREFIX+bankAccount.getAccountNumber();

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(stringRedisTemplate.opsForValue().get(key)).thenReturn(failedAttemptValue);
            when(passwordEncoder.matches(rawPin, bankAccount.getPin())).thenReturn(false);

            assertThrows(IncorrectPinException.class, () -> commonAccountUtil.validatePin(bankAccount, rawPin));
            assertEquals(AccountStatus.ACTIVE, bankAccount.getStatus());
            verify(valueOperations, times(1)).increment(key);
            verify(stringRedisTemplate, never()).delete(key);
        }

        @Test
        public void shouldThrowExceptionAndSetValueInRedis_whenPinIsIncorrectForFirstTime(){
            bankAccount.setStatus(AccountStatus.ACTIVE);
            String rawPin = "4321";
            String key = FAILED_ATTEMPT_KEY_PREFIX+bankAccount.getAccountNumber();

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(stringRedisTemplate.opsForValue().get(key)).thenReturn(null);
            when(passwordEncoder.matches(rawPin, bankAccount.getPin())).thenReturn(false);

            assertThrows(IncorrectPinException.class, () -> commonAccountUtil.validatePin(bankAccount, rawPin));
            assertEquals(AccountStatus.ACTIVE, bankAccount.getStatus());
            verify(valueOperations, times(1)).set(key, String.valueOf(1), Duration.ofHours(24));
            verify(stringRedisTemplate, never()).delete(key);
        }



        @SuppressWarnings("JUnitMalformedDeclaration")
        @ParameterizedTest
        @ValueSource(strings = {"1", "2", "3", "4"})
        @NullSource
        public void shouldValidatePinSuccessfully_whenPinIsCorrect(String failedAttemptValue){
            bankAccount.setStatus(AccountStatus.ACTIVE);
            String rawPin = "1234";
            String key = FAILED_ATTEMPT_KEY_PREFIX+bankAccount.getAccountNumber();

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(stringRedisTemplate.opsForValue().get(FAILED_ATTEMPT_KEY_PREFIX+bankAccount.getAccountNumber())).thenReturn(failedAttemptValue);
            when(passwordEncoder.matches(rawPin, bankAccount.getPin())).thenReturn(true);

            assertDoesNotThrow(() -> commonAccountUtil.validatePin(bankAccount, rawPin));
            assertEquals(AccountStatus.ACTIVE, bankAccount.getStatus());
            verify(stringRedisTemplate, times(1)).delete(key);

        }

    }

}
