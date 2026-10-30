package com.adnan.bankapp.banking.transaction.util;

import com.adnan.bankapp.banking.account.entity.AccountType;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.enums.AccountStatus;
import com.adnan.bankapp.banking.exception.TransactionLimitExceededException;
import com.adnan.bankapp.banking.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class TransactionValidatorTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionValidator transactionValidator;

    private AccountType accountTypeSetUp(){
        AccountType accountType = new AccountType();
        accountType.setDailySendTransactionAmountLimit(BigDecimal.valueOf(100000));
        accountType.setDailySendTransactionCountLimit(20L);
        accountType.setDailyReceiveTransactionAmountLimit(BigDecimal.valueOf(100000));
        accountType.setDailyReceiveTransactionCountLimit(20L);
        accountType.setMaxSendTransactionAmount(BigDecimal.valueOf(100000));
        return accountType;
    }

    private BankAccount bankAccountSetUp(String accountNumber, BigDecimal balance){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setBalance(balance);
        bankAccount.setAccountType(accountTypeSetUp());
        return  bankAccount;
    }

    @Test
    public void shouldThrowException_whenSenderTransactionAmountLimitExceeded(){


        BankAccount sender = bankAccountSetUp("1122334455", BigDecimal.valueOf(20000));
        BankAccount receiver = bankAccountSetUp("6677889900", BigDecimal.valueOf(5000));
        BigDecimal amount = BigDecimal.valueOf(15000);
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime cutOffTime = time.minusHours(24);

        when(transactionRepository.getTotalSentAmountSince(sender.getAccountNumber(),cutOffTime)).thenReturn(BigDecimal.valueOf(90000));

        assertThrows(TransactionLimitExceededException.class, () -> transactionValidator.validate(sender, receiver, amount, time));
    }

    @Test
    public void shouldThrowException_whenSenderTransactionCountLimitExceeded(){


        BankAccount sender = bankAccountSetUp("1122334455", BigDecimal.valueOf(20000));
        BankAccount receiver = bankAccountSetUp("6677889900", BigDecimal.valueOf(5000));
        BigDecimal amount = BigDecimal.valueOf(15000);
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime cutOffTime = time.minusHours(24);

        when(transactionRepository.getTotalSentAmountSince(sender.getAccountNumber(), cutOffTime)).thenReturn(BigDecimal.valueOf(50000));
        when(transactionRepository.countSentTransactionsSince(sender.getAccountNumber(), cutOffTime)).thenReturn(20L);

        assertThrows(TransactionLimitExceededException.class, () -> transactionValidator.validate(sender, receiver, amount, time));
    }

    @Test
    public void shouldThrowException_whenReceiverTransactionAmountLimitExceeded(){

        BankAccount sender = bankAccountSetUp("1122334455", BigDecimal.valueOf(20000));
        BankAccount receiver = bankAccountSetUp("6677889900", BigDecimal.valueOf(5000));
        BigDecimal amount = BigDecimal.valueOf(15000);
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime cutOffTime = time.minusHours(24);

        when(transactionRepository.getTotalSentAmountSince(sender.getAccountNumber(), cutOffTime)).thenReturn(BigDecimal.valueOf(0));
        when(transactionRepository.countSentTransactionsSince(sender.getAccountNumber(), cutOffTime)).thenReturn(0L);
        when(transactionRepository.getTotalReceiveAmountSince(receiver.getAccountNumber(), cutOffTime)).thenReturn(BigDecimal.valueOf(90000L));

        assertThrows(TransactionLimitExceededException.class, () -> transactionValidator.validate(sender, receiver, amount, time));
    }

    @Test
    public void shouldThrowException_whenReceiverTransactionCountLimitExceeded(){

        BankAccount sender = bankAccountSetUp("1122334455", BigDecimal.valueOf(20000));
        BankAccount receiver = bankAccountSetUp("6677889900", BigDecimal.valueOf(5000));
        BigDecimal amount = BigDecimal.valueOf(15000);
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime cutOffTime = time.minusHours(24);

        when(transactionRepository.getTotalSentAmountSince(sender.getAccountNumber(), cutOffTime)).thenReturn(BigDecimal.valueOf(0));
        when(transactionRepository.countSentTransactionsSince(sender.getAccountNumber(), cutOffTime)).thenReturn(0L);
        when(transactionRepository.getTotalReceiveAmountSince(receiver.getAccountNumber(), cutOffTime)).thenReturn(BigDecimal.valueOf(50000));
        when(transactionRepository.countReceiveTransactionsSince(receiver.getAccountNumber(), cutOffTime)).thenReturn(20L);

        assertThrows(TransactionLimitExceededException.class, () -> transactionValidator.validate(sender, receiver, amount, time));
    }

    @Test
    public void shouldValidateTransactionSuccessfully(){

        BankAccount sender = bankAccountSetUp("1122334455", BigDecimal.valueOf(20000));
        BankAccount receiver = bankAccountSetUp("6677889900", BigDecimal.valueOf(5000));
        BigDecimal amount = BigDecimal.valueOf(15000);
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime cutOffTime = time.minusHours(24);

        when(transactionRepository.getTotalSentAmountSince(sender.getAccountNumber(), cutOffTime)).thenReturn(BigDecimal.valueOf(85000));
        when(transactionRepository.countSentTransactionsSince(sender.getAccountNumber(), cutOffTime)).thenReturn(10L);
        when(transactionRepository.getTotalReceiveAmountSince(receiver.getAccountNumber(), cutOffTime)).thenReturn(BigDecimal.valueOf(80000));
        when(transactionRepository.countReceiveTransactionsSince(receiver.getAccountNumber(), cutOffTime)).thenReturn(10L);

        assertDoesNotThrow(() ->transactionValidator.validate(sender, receiver, amount, time));
    }
}
