package com.adnan.bankapp.banking.transaction.util;

import com.adnan.bankapp.banking.account.entity.AccountType;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.exception.TransactionLimitExceededException;
import com.adnan.bankapp.banking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionValidator {

    private final TransactionRepository transactionRepository;

    public void validate(BankAccount sender, BankAccount receiver, BigDecimal amount, LocalDateTime time) {

        log.info("Validating transaction | sender: {} | receiver: {} | amount: {}", sender.getAccountNumber(), receiver.getAccountNumber(), amount);
        AccountType senderAccountType = sender.getAccountType();
        AccountType receiverAccountType = receiver.getAccountType();

        LocalDateTime cutOffTime = time.minusHours(24);


        //validate sender
        BigDecimal senderActualLimit = senderAccountType.getDailySendTransactionAmountLimit();
        BigDecimal senderUsedLimit = transactionRepository.getTotalSentAmountSince(sender.getAccountNumber(), cutOffTime);
        // 24 hour transaction volume
        BigDecimal senderAvailableLimit = senderActualLimit.subtract(senderUsedLimit);

        // if limit-volume<amount = fail
        if (senderAvailableLimit.compareTo(amount) < 0){
            log.warn("Transaction failed | reason: sender transaction limit is exceeded | sender: {} | receiver: {} | amount: {}", sender.getAccountNumber(), receiver.getAccountNumber(), amount);
            throw new TransactionLimitExceededException("Your 24 hours transaction limit is exceeded. Please try again later.");
        }

        // 24 hour transaction count
        Long senderActualTransactionCount = senderAccountType.getDailySendTransactionCountLimit();
        Long senderUsedTransactionCount = transactionRepository.countSentTransactionsSince(sender.getAccountNumber(), cutOffTime);
        long senderAvailableTransactionCount = senderActualTransactionCount - senderUsedTransactionCount;

        // count - currentCount < 1 = fail
        if (senderAvailableTransactionCount < 1){
            log.warn("Transaction failed | reason: sender transaction count is exceeded | sender: {} | receiver: {} | amount: {}", sender.getAccountNumber(), receiver.getAccountNumber(), amount);
            throw new TransactionLimitExceededException("Your 24 hours transaction count is exceeded. Please try again later.");
        }


        // validate receiver
        BigDecimal receiverActualLimit = receiverAccountType.getDailyReceiveTransactionAmountLimit();
        BigDecimal receiverUsedLimit = transactionRepository.getTotalReceiveAmountSince(receiver.getAccountNumber(), cutOffTime);
        // 24 hour transaction volume
        BigDecimal receiverAvailableLimit = receiverActualLimit.subtract(receiverUsedLimit);

        // if limit-volume<amount = fail
        if (receiverAvailableLimit.compareTo(amount) < 0){
            log.warn("Transaction failed | reason: receiver transaction limit is exceeded | sender: {} | receiver: {} | amount: {}", sender.getAccountNumber(), receiver.getAccountNumber(), amount);
            throw new TransactionLimitExceededException("Receiver 24 hours transaction limit is exceeded. Please try again later.");
        }

        // 24 hour transaction count
        Long receiverActualTransactionCount = receiverAccountType.getDailyReceiveTransactionCountLimit();
        Long receiverUsedTransactionCount = transactionRepository.countReceiveTransactionsSince(receiver.getAccountNumber(), cutOffTime);
        long receiverAvailableTransactionCount = receiverActualTransactionCount - receiverUsedTransactionCount;

        // count - currentCount < 1 = fail
        if (receiverAvailableTransactionCount < 1){
            log.warn("Transaction failed | reason: receiver transaction count is exceeded | sender: {} | receiver: {} | amount: {}", sender.getAccountNumber(), receiver.getAccountNumber(), amount);
            throw new TransactionLimitExceededException("Receiver 24 hours transaction count is exceeded. Please try again later.");
        }

        log.info("Transaction validation completed successfully | sender: {} | receiver: {} | amount: {}", sender.getAccountNumber(), receiver.getAccountNumber(), amount);

    }
}
