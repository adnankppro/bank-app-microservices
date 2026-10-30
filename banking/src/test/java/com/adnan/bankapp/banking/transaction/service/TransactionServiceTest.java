package com.adnan.bankapp.banking.transaction.service;


import com.adnan.bankapp.banking.account.entity.AccountType;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.enums.AccountStatus;
import com.adnan.bankapp.banking.account.service.AccountService;
import com.adnan.bankapp.banking.exception.InsufficientBalanceException;
import com.adnan.bankapp.banking.exception.InvalidAccountStatusException;
import com.adnan.bankapp.banking.transaction.dto.DepositRequest;
import com.adnan.bankapp.banking.transaction.dto.TransactionRequest;
import com.adnan.bankapp.banking.transaction.entity.Transaction;
import com.adnan.bankapp.banking.transaction.enums.TransactionPurpose;
import com.adnan.bankapp.banking.transaction.repository.TransactionRepository;
import com.adnan.bankapp.banking.transaction.util.TransactionValidator;
import com.adnan.bankapp.banking.util.CommonAccountUtil;
import com.bankapp.common.dto.MyDetails;
import com.bankapp.common.exception.InvalidRequestException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CommonAccountUtil commonAccountUtil;

    @Mock
    private TransactionValidator transactionValidator;

    @InjectMocks
    private TransactionService transactionService;

    private AccountType accountTypeSetUp(BigDecimal maxSendAmount){
        AccountType accountType = new AccountType();
        accountType.setMaxSendTransactionAmount(maxSendAmount);
        return accountType;
    }

    private BankAccount bankAccountSetUp(Long accountId, Long userId, String accountNumber, BigDecimal balance, AccountStatus status){
        AccountType accountType = accountTypeSetUp(BigDecimal.valueOf(50000));
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(accountId);
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountType(accountType);
        bankAccount.setStatus(status);
        bankAccount.setBalance(balance);
        bankAccount.setUserId(userId);
        return  bankAccount;
    }

    private Transaction setUpTransaction(BankAccount sender, BankAccount receiver, BigDecimal amount, TransactionPurpose purpose, Long initiatedBy){
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setTransactionPurpose(purpose);
        transaction.setInitiatedByUserId(initiatedBy);
        return  transaction;
    }

    @Nested
    public class sendTests{


        @Test
        public void shouldThrowInvalidRequestException_WhenSenderAndReceiverAccountNumberAreSame(){

            TransactionRequest request = new TransactionRequest("0123456789", "0123456789", "1234", BigDecimal.valueOf(100), TransactionPurpose.PERSONAL);
            MyDetails userDetails = new MyDetails("adnankppro@gmail.com", 1L);

            assertThrows(InvalidRequestException.class,() -> transactionService.send(request, userDetails));
            verify(accountService, never()).getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), userDetails.id());
            verify(accountService, never()).getBankAccountByAccountNumberForUpdate(request.receiver());
            verify(transactionRepository, never()).save(any());
        }

        @Test
        public void shouldThrowInvalidRequestException_whenAmountIsGreaterThanLimit(){
            TransactionRequest request = new TransactionRequest("0123456789", "1122334455", "1234", BigDecimal.valueOf(60000), TransactionPurpose.PERSONAL);
            MyDetails userDetails = new MyDetails("adnankppro@gmail.com", 1L);

            BankAccount sender = bankAccountSetUp(1L, 1L, "1122334455", BigDecimal.valueOf(100000), AccountStatus.ACTIVE);
            BankAccount receiver = bankAccountSetUp(2L, 2L, "0123456789", BigDecimal.valueOf(1000), AccountStatus.ACTIVE);

            when(accountService.getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), userDetails.id())).thenReturn(sender);
            when(accountService.getBankAccountByAccountNumberForUpdate(request.receiver())).thenReturn(receiver);

            assertThrows(InvalidRequestException.class,() -> transactionService.send(request, userDetails));
            assertEquals(0, sender.getBalance().compareTo(BigDecimal.valueOf(100000)));
            assertEquals(0, receiver.getBalance().compareTo(BigDecimal.valueOf(1000)));
            verify(transactionRepository, never()).save(any());
        }

        @Test
        public void shouldThrowInvalidAccountStatusException_whenSenderAccountIsNotActive(){
            TransactionRequest request = new TransactionRequest("0123456789", "1122334455", "1234", BigDecimal.valueOf(1000), TransactionPurpose.PERSONAL);
            MyDetails userDetails = new MyDetails("adnankppro@gmail.com", 1L);

            BankAccount sender = bankAccountSetUp(1L, 1L, "1122334455", BigDecimal.valueOf(100000), AccountStatus.BLOCKED);
            BankAccount receiver = bankAccountSetUp(2L, 2L, "0123456789", BigDecimal.valueOf(1000), AccountStatus.ACTIVE);

            when(accountService.getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), userDetails.id())).thenReturn(sender);
            when(accountService.getBankAccountByAccountNumberForUpdate(request.receiver())).thenReturn(receiver);

            assertThrows(InvalidAccountStatusException.class,() -> transactionService.send(request, userDetails));
            assertEquals(0, sender.getBalance().compareTo(BigDecimal.valueOf(100000)));
            assertEquals(0, receiver.getBalance().compareTo(BigDecimal.valueOf(1000)));
            verify(transactionRepository, never()).save(any());
        }

        @Test
        public void shouldThrowInsufficientBalanceException_whenSenderBalanceIsInsufficient(){
            TransactionRequest request = new TransactionRequest("0123456789", "1122334455", "1234", BigDecimal.valueOf(20000), TransactionPurpose.PERSONAL);
            MyDetails userDetails = new MyDetails("adnankppro@gmail.com", 1L);

            BankAccount sender = bankAccountSetUp(1L, 1L, "1122334455", BigDecimal.valueOf(10000), AccountStatus.ACTIVE);
            BankAccount receiver = bankAccountSetUp(2L, 2L, "0123456789", BigDecimal.valueOf(1000), AccountStatus.ACTIVE);

            when(accountService.getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), userDetails.id())).thenReturn(sender);
            when(accountService.getBankAccountByAccountNumberForUpdate(request.receiver())).thenReturn(receiver);

            assertThrows(InsufficientBalanceException.class,() -> transactionService.send(request, userDetails));
            assertEquals(0, sender.getBalance().compareTo(BigDecimal.valueOf(10000)));
            assertEquals(0, receiver.getBalance().compareTo(BigDecimal.valueOf(1000)));
            verify(transactionRepository, never()).save(any());

        }

        @Test
        public void shouldThrowInvalidAccountStatusException_whenReceiverAccountIsNotActive(){
            TransactionRequest request = new TransactionRequest("0123456789", "1122334455", "1234", BigDecimal.valueOf(1000), TransactionPurpose.PERSONAL);
            MyDetails userDetails = new MyDetails("adnankppro@gmail.com", 1L);

            BankAccount sender = bankAccountSetUp(1L, 1L, "1122334455", BigDecimal.valueOf(100000), AccountStatus.ACTIVE);
            BankAccount receiver = bankAccountSetUp(2L, 2L, "0123456789", BigDecimal.valueOf(1000), AccountStatus.BLOCKED);


            when(accountService.getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), userDetails.id())).thenReturn(sender);
            when(accountService.getBankAccountByAccountNumberForUpdate(request.receiver())).thenReturn(receiver);
            doNothing().when(commonAccountUtil).validatePin(any(), any());

            assertThrows(InvalidAccountStatusException.class,() -> transactionService.send(request, userDetails));
            assertEquals(0, sender.getBalance().compareTo(BigDecimal.valueOf(100000)));
            assertEquals(0, receiver.getBalance().compareTo(BigDecimal.valueOf(1000)));
            verify(commonAccountUtil, times(1)).validatePin(sender, request.pin());
            verify(transactionRepository, never()).save(any());
        }

        @Test
        public void shouldSendMoney(){
            TransactionRequest request = new TransactionRequest("1122334455", "0123456789", "1234", BigDecimal.valueOf(1000), TransactionPurpose.PERSONAL);
            MyDetails userDetails = new MyDetails("adnankppro@gmail.com", 1L);

            BankAccount sender = bankAccountSetUp(1L, 1L, "0123456789", BigDecimal.valueOf(100000), AccountStatus.ACTIVE);
            BankAccount receiver = bankAccountSetUp(2L, 2L, "1122334455", BigDecimal.valueOf(1000), AccountStatus.ACTIVE);

            Transaction transaction = setUpTransaction(sender, receiver, request.amount(), request.purpose(), userDetails.id());

            when(accountService.getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), userDetails.id())).thenReturn(sender);
            when(accountService.getBankAccountByAccountNumberForUpdate(request.receiver())).thenReturn(receiver);
            when(transactionRepository.save(any())).thenReturn(transaction);
            doNothing().when(transactionValidator).validate(any(), any(), any(), any());
            doNothing().when(commonAccountUtil).validatePin(any(), any());

            transactionService.send(request, userDetails);

            assertEquals(0, sender.getBalance().compareTo(BigDecimal.valueOf(99000)));
            assertEquals(0, receiver.getBalance().compareTo(BigDecimal.valueOf(2000)));
            verify(commonAccountUtil, times(1)).validatePin(sender, request.pin());
            verify(transactionRepository, times(1)).save(any());
        }

    }

    @Nested
    public class depositTests{

        @Test
        public void shouldThrowInvalidAccountStatusException_whenAccountIsNotActive(){
            BankAccount receiver = bankAccountSetUp(2L, 2L, "1122334455", BigDecimal.valueOf(1000), AccountStatus.BLOCKED);
            DepositRequest request = new DepositRequest("1122334455", BigDecimal.valueOf(5000));

            MyDetails admin = new MyDetails("adnankppro@gmail.com", 1L);

            when(accountService.getBankAccountByAccountNumberForUpdate(request.accountNumber())).thenReturn(receiver);

            assertThrows(InvalidAccountStatusException.class, () -> transactionService.deposit(request, admin));
            assertEquals(0, receiver.getBalance().compareTo(BigDecimal.valueOf(1000)));
            verify(transactionRepository, never()).save(any());

        }

        @Test
        public void shouldDeposit(){
            BankAccount receiver = bankAccountSetUp(2L, 2L, "1122334455", BigDecimal.valueOf(1000), AccountStatus.ACTIVE);
            DepositRequest request = new DepositRequest("1122334455", BigDecimal.valueOf(5000));
            MyDetails admin = new MyDetails("adnankppro@gmail.com", 1L);
            Transaction transaction = setUpTransaction(null, receiver, request.amount(), TransactionPurpose.DEPOSIT, admin.id());


            when(accountService.getBankAccountByAccountNumberForUpdate(request.accountNumber())).thenReturn(receiver);
            when(transactionRepository.save(any())).thenReturn(transaction);

            transactionService.deposit(request, admin);

            assertEquals(0, receiver.getBalance().compareTo(BigDecimal.valueOf(6000)));
            verify(transactionRepository, times(1)).save(any());
        }

    }
}
