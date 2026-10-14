package com.adnan.bankapp.banking.account.repository;

import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.bankapp.common.enums.BankAccountType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<BankAccount, Long> {
    boolean existsByAccountNumber(String accountNumber);

    boolean existsByUserIdAndAccountType(Long userId, BankAccountType bankAccountType);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    Optional<BankAccount> findByAccountNumberAndUserId(String s, Long userId);

    @Query("""
    SELECT b FROM BankAccount b
    where b.accountNumber = :accountNumber AND
    b.userId = :userId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BankAccount> findByAccountNumberAndUserIdForUpdate(@Param("accountNumber") String accountNumber,@Param("userId") Long userId);

    @Query("""
    SELECT b FROM BankAccount b
    where b.accountNumber = :accountNumber
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BankAccount> findByAccountNumberForUpdate(@Param("userId") String accountNumber);

    boolean existsByAccountNumberAndUserId(String accountNumber, Long userId);
}
