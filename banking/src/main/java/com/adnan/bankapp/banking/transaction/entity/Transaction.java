package com.adnan.bankapp.banking.transaction.entity;

import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.transaction.enums.TransactionPurpose;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private BankAccount sender;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private BankAccount receiver;

    private Double amount;

    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    private TransactionPurpose transactionPurpose;
}
