package com.adnan.bankapp.banking.transaction.mapper;

import com.adnan.bankapp.banking.transaction.dto.TransactionResponse;
import com.adnan.bankapp.banking.transaction.entity.Transaction;
import com.adnan.bankapp.banking.transaction.enums.TransactionType;

public class TransactionMapper {

    public static TransactionResponse toTransactionResponse(Transaction transaction, String accountNumber){
        TransactionType transactionType;
        String counterPartyName;
        String counterPartyAccountNumber;

        if (transaction.getSender().getAccountNumber().equals(accountNumber) && transaction.getReceiver().getAccountNumber().equals(accountNumber)){
            transactionType = TransactionType.DEPOSIT;
            counterPartyName = "CASH_DEPOSIT";
            counterPartyAccountNumber = "CASH_DEPOSIT";
        }else if(transaction.getSender().getAccountNumber().equals(accountNumber)){
            transactionType = TransactionType.SEND;
            counterPartyName = transaction.getReceiver().getAccountHolderName();
            counterPartyAccountNumber = transaction.getReceiver().getAccountNumber();
        }else {
            transactionType = TransactionType.RECEIVE;
            counterPartyName = transaction.getSender().getAccountHolderName();
            counterPartyAccountNumber = transaction.getSender().getAccountNumber();
        }
        return new TransactionResponse(transactionType, counterPartyName, counterPartyAccountNumber, transaction.getAmount(), transaction.getTime(), transaction.getTransactionPurpose());    }

}
