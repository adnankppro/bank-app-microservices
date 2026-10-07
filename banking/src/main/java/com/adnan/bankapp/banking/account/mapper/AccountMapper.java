package com.adnan.bankapp.banking.account.mapper;


import com.adnan.bankapp.banking.account.dto.AccountDetailsResponse;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDetailsResponse toAccountDetailsResponse(BankAccount bankAccount){
        return new AccountDetailsResponse(bankAccount.getAccountNumber(), bankAccount.getBranchCode(), bankAccount.getUserId(), bankAccount.getBalance());
    }

}
