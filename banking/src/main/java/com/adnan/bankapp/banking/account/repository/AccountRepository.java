package com.adnan.bankapp.banking.account.repository;

import com.adnan.bankapp.banking.account.entity.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<BankAccount, Long> {
    boolean existsByAccountNumber(String accountNumber);

    boolean existsByUserIdAndAccountTypeId(Long userId, Long accountTypeId);

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
    Optional<BankAccount> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    boolean existsByAccountNumberAndUserId(String accountNumber, Long userId);

    @Query("SELECT b.accountHolderName FROM BankAccount b WHERE b.accountNumber = :accountNumber")
    Optional<String> getAccountHolderNameByAccountNumber(@Param("accountNumber") String accountNumber);
}
