package com.adnan.bankapp.banking.account.entity;

import com.bankapp.common.enums.BankAccountType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_type_name", nullable = false, unique = true)
    private String accountTypeName;

    @Column(name = "daily_send_transaction_amount_limit", nullable = false)
    private BigDecimal dailySendTransactionAmountLimit;

    @Column(name = "daily_send_transaction_count_limit", nullable = false)
    private Integer dailySendTransactionCountLimit;

    @Column(name = "daily_send_transaction_amount_limit", nullable = false)
    private BigDecimal dailyReceiveTransactionAmountLimit;

    @Column(name = "daily_send_transaction_count_limit", nullable = false)
    private Integer dailyReceiveTransactionCountLimit;

    @Column(name = "max_send_transaction_amount", nullable = false)
    private BigDecimal maxSendTransactionAmount;

}
