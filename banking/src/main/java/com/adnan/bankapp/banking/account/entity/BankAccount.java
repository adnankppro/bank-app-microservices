package com.adnan.bankapp.banking.account.entity;

import com.adnan.bankapp.banking.account.AccountStatus;
import com.bankapp.common.enums.BankAccountType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private BankAccountType accountType;

    private BigDecimal balance;

    private String branchCode;

    @Column(length = 4)
    private String pin;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
