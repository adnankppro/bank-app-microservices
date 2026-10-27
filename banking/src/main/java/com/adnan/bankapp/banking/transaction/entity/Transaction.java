package com.adnan.bankapp.banking.transaction.entity;

import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.transaction.enums.TransactionPurpose;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long initiatedByUserId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private BankAccount sender;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber", nullable = false)
    private BankAccount receiver;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionPurpose transactionPurpose;
}
