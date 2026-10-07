package com.adnan.bankapp.banking.account.repository;

import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.bankapp.common.enums.BankAccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<BankAccount, Long> {
    boolean existsByAccountNumber(String accountNumber);

    boolean existsByUserIdAndAccountType(Long userId, BankAccountType bankAccountType);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    Optional<BankAccount> findByAccountNumberAndUserId(String s, Long userId);
}
