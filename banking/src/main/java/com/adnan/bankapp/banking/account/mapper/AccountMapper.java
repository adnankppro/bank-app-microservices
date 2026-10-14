package com.adnan.bankapp.banking.account.mapper;


import com.adnan.bankapp.banking.account.dto.AccountDetailsResponse;
import com.adnan.bankapp.banking.account.dto.AccountTypeDetails;
import com.adnan.bankapp.banking.account.dto.CreateAccountTypeRequest;
import com.adnan.bankapp.banking.account.entity.AccountType;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDetailsResponse toAccountDetailsResponse(BankAccount bankAccount){
        return new AccountDetailsResponse(bankAccount.getAccountNumber(), bankAccount.getBranchCode(), bankAccount.getUserId(), bankAccount.getBalance());
    }

    public AccountType toAccountType(CreateAccountTypeRequest request) {
        AccountType accountType = new AccountType();
        accountType.setAccountTypeName(request.accountTypeName());
        accountType.setDailyReceiveTransactionCountLimit(request.dailyReceiveTransactionCountLimit());
        accountType.setDailySendTransactionAmountLimit(request.dailySendTransactionAmountLimit());
        accountType.setDailyReceiveTransactionAmountLimit(request.dailyReceiveTransactionAmountLimit());
        accountType.setMaxSendTransactionAmount(request.maxSendTransactionAmount());
        accountType.setDailySendTransactionCountLimit(request.dailySendTransactionCountLimit());
        return accountType;
    }

    public AccountTypeDetails toAccountTypeDetails(AccountType accountType) {
        return new AccountTypeDetails(accountType.getAccountTypeName(), accountType.getDailySendTransactionAmountLimit(), accountType.getDailySendTransactionCountLimit(), accountType.getDailyReceiveTransactionAmountLimit(), accountType.getDailyReceiveTransactionCountLimit(), accountType.getMaxSendTransactionAmount());
    }
}
