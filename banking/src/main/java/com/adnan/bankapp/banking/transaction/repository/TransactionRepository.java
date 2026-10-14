package com.adnan.bankapp.banking.transaction.repository;

import com.adnan.bankapp.banking.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("""
           SELECT coalesce(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.sender.accountNumber = :accountNumber AND
           t.time > :cutOffTime
           """)
    BigDecimal getTotalSentAmountSince(@Param("accountNumber") String accountNumber, @Param("cutOffTime") LocalDateTime cutOffTime);

    @Query("""
           SELECT coalesce(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.receiver.accountNumber = :accountNumber AND
           t.time > :cutOffTime
           """)
    BigDecimal getTotalReceiveAmountSince(@Param("accountNumber") String accountNumber, @Param("cutOffTime") LocalDateTime cutOffTime);


    @Query("""
        SELECT COUNT(t) FROM Transaction t
        WHERE t.sender.accountNumber = :accountNumber AND
        t.time > :cutOffTime
""")
    Integer countSentTransactionsSince(@Param("accountNumber") String accountNumber, @Param("cutOffTime") LocalDateTime cutOffTime);

    @Query("""
        SELECT COUNT(t) FROM Transaction t
        WHERE t.receiver.accountNumber = :accountNumber AND
        t.time > :cutOffTime
""")
    Integer countReceiveTransactionsSince(@Param("accountNumber") String accountNumber, @Param("cutOffTime") LocalDateTime cutOffTime);

}
