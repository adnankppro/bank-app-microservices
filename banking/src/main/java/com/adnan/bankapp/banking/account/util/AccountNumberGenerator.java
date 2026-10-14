package com.adnan.bankapp.banking.account.util;

import com.adnan.bankapp.banking.exception.DuplicateAccountNumberException;
import com.adnan.bankapp.banking.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final long MIN_ACCOUNT_NUMBER = 1000000000L;
    private static final long ACCOUNT_NUMBER_RANGE = 9000000000L;
    private static final int MAX_ATTEMPTS =3;


    public String generate(){
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++){
            long number = MIN_ACCOUNT_NUMBER + secureRandom.nextLong(ACCOUNT_NUMBER_RANGE);
            String accountNumber = String.valueOf(number);
            if(!accountRepository.existsByAccountNumber(accountNumber)){
                return accountNumber;
            }
            log.warn("Account number collision while creating new account | attempt: {}", attempt);
        }
        throw new DuplicateAccountNumberException("Unexpected error occurred. Please try again.");
    }

}
