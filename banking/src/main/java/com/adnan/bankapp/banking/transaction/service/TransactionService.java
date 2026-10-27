package com.adnan.bankapp.banking.transaction.service;

import com.adnan.bankapp.banking.account.enums.AccountStatus;
import com.adnan.bankapp.banking.account.entity.AccountType;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.service.AccountService;
import com.adnan.bankapp.banking.exception.*;
import com.adnan.bankapp.banking.transaction.dto.*;
import com.adnan.bankapp.banking.transaction.entity.Transaction;
import com.adnan.bankapp.banking.transaction.enums.TransactionPurpose;
import com.adnan.bankapp.banking.transaction.enums.TransactionType;
import com.adnan.bankapp.banking.transaction.mapper.TransactionMapper;
import com.adnan.bankapp.banking.transaction.repository.TransactionRepository;
import com.adnan.bankapp.banking.transaction.specification.TransactionSpecification;
import com.adnan.bankapp.banking.util.CommonAccountUtil;
import com.bankapp.common.dto.MyDetails;
import com.bankapp.common.exception.InvalidRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CommonAccountUtil commonAccountUtil;

    @Transactional(noRollbackFor = PinValidationLimitExceededException.class)
    public TransactionResponse send(TransactionRequest request, MyDetails myDetails) {
        log.info("Request received for transaction | sender: {} | receiver: {} | amount: {} | purpose: {}", request.sender(), request.receiver(), request.amount(), request.purpose());

        LocalDateTime time = LocalDateTime.now();

        if(request.sender().equals(request.receiver())){
            log.warn("Transaction failed | reason: sender and receiver account numbers are same | sender: {} | receiver: {} | amount: {} | purpose: {}", request.sender(), request.receiver(), request.amount(), request.purpose());
            throw new InvalidRequestException("Sender and receiver account can't be same.");
        }

        BankAccount sender, receiver;

        int diff = request.sender().compareTo(request.receiver());// to prevent deadlock
        if (diff<0){
            sender = accountService.getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), myDetails.id());
            receiver = accountService.getBankAccountByAccountNumberForUpdate(request.receiver());
        }else {
            receiver = accountService.getBankAccountByAccountNumberForUpdate(request.receiver());
            sender = accountService.getBankAccountByAccountNumberAndUserIdForUpdate(request.sender(), myDetails.id());
        }

        if (sender.getAccountType().getMaxSendTransactionAmount().compareTo(request.amount()) < 0){
            log.warn("Transaction failed | reason: requested amount is greater than limit | sender: {} | receiver: {} | amount: {} | purpose: {}", request.sender(), request.receiver(), request.amount(), request.purpose());
            throw new InvalidRequestException("Maximum amount per transaction permitted for your account is "+sender.getAccountType().getMaxSendTransactionAmount()+" rupees.");
        }

        if (!sender.getStatus().equals(AccountStatus.ACTIVE)){
            log.warn("Transaction failed | reason: sender account is not active | sender: {} | receiver: {} | amount: {} | purpose: {}", request.sender(), request.receiver(), request.amount(), request.purpose());
            throw new InvalidAccountStatusException("Your account is not active. Please contact branch.");
        }

        commonAccountUtil.validatePin(sender, request.pin());// this will throw incorrect pin exception, if the limit exceeded, it will also block the account, that's why noRollbackFor PinValidationLimitExceededException

        if (sender.getBalance().compareTo(request.amount())<0){
            log.warn("Transaction failed | reason: insufficient balance | sender: {} | receiver: {} | amount: {} | purpose: {}", request.sender(), request.receiver(), request.amount(), request.purpose());
            throw new InsufficientBalanceException("Insufficient balance.");
        }



        if (!receiver.getStatus().equals(AccountStatus.ACTIVE)){
            log.warn("Transaction failed | reason: receiver account is not active | sender: {} | receiver: {} | amount: {} | purpose: {}", request.sender(), request.receiver(), request.amount(), request.purpose());
            throw new InvalidAccountStatusException("Receiver account is not active.");
        }

        validateTransaction(sender, receiver, request.amount(), time);// check transaction limit

        // update balance
        sender.setBalance(sender.getBalance().subtract(request.amount()));
        receiver.setBalance(receiver.getBalance().add(request.amount()));

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(request.amount());
        transaction.setTime(time);
        transaction.setTransactionPurpose(request.purpose());
        transaction.setInitiatedByUserId(myDetails.id());
        transaction = transactionRepository.save(transaction);

        log.info("Transaction completed successfully | sender: {} | receiver: {} | amount: {} | purpose: {}", request.sender(), request.receiver(), request.amount(), request.purpose());

        return new TransactionResponse( TransactionType.SEND,receiver.getAccountHolderName(), receiver.getAccountNumber(), transaction.getAmount(), transaction.getTime(), transaction.getTransactionPurpose());
    }


    private void validateTransaction(BankAccount sender, BankAccount receiver, BigDecimal amount, LocalDateTime time) {

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

    @Transactional
    public DepositResponse deposit(@Valid DepositRequest request, MyDetails admin) {
        log.info("Request received for deposit | accountNumber: {} | amount: {} | adminId: {}", request.accountNumber(), request.amount(), admin.id());
        LocalDateTime time = LocalDateTime.now();
        BankAccount account = accountService.getBankAccountByAccountNumberForUpdate(request.accountNumber());
        if (!account.getStatus().equals(AccountStatus.ACTIVE)){
            log.warn("Deposit failed | reason: account is not active | accountNumber: {} | amount: {} | adminId: {}", request.accountNumber(), request.amount(), admin.id());
            throw new InvalidAccountStatusException("Recipient account is not active");
        }
        Transaction transaction = new Transaction();
        transaction.setReceiver(account);
        transaction.setSender(null);
        transaction.setAmount(request.amount());
        transaction.setTransactionPurpose(TransactionPurpose.DEPOSIT);
        transaction.setInitiatedByUserId(admin.id());
        transaction.setTime(time);
        transaction = transactionRepository.save(transaction);

        account.setBalance(account.getBalance().add(request.amount()));
        log.info("Deposit completed successfully | accountNumber: {} | amount: {} | adminId: {}", request.accountNumber(), request.amount(), admin.id());
        return new DepositResponse(request.accountNumber(), transaction.getAmount(), transaction.getTime());
    }

    @Transactional(readOnly = true)// for logged-in user
    public Page<TransactionResponse> getTransactionHistoryForUser(String accountNumber, Long userId, TransactionFilter filter, Pageable pageable) {
        log.info("Request received to fetch transaction history | accountNumber: {} | userId: {} ", accountNumber, userId);
        if (!accountService.existsByAccountNumberAndUserId(accountNumber, userId)){
            log.info("Transaction history fetching failed | reason: invalid account number | accountNumber: {} | userId: {} ", accountNumber, userId);
            throw new InvalidAccountNumberException("Invalid account number.");
        }


        Specification<Transaction> specification = TransactionSpecification.build(filter, accountNumber);
        // pageable filter
        Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);

        log.info("Transaction history fetch successfully | accountNumber: {} | userId: {} ", accountNumber, userId);
        return transactions.map(t -> TransactionMapper.toTransactionResponse(t, accountNumber));
    }

    @Transactional(readOnly = true)// for logged-in user
    public Page<TransactionResponse> getTransactionHistoryForAdmin(String accountNumber, Long adminId, TransactionFilter filter, Pageable pageable) {
        log.info("Request received to fetch transaction history by admin | accountNumber: {} | adminId: {} ", accountNumber, adminId);
        if (!accountService.existsByAccountNumber(accountNumber)){
            log.info("Transaction history fetching by admin failed | reason: invalid account number | accountNumber: {} | adminId: {} ", accountNumber, adminId);
            throw new InvalidAccountNumberException("Invalid account number.");
        }


        Specification<Transaction> specification = TransactionSpecification.build(filter, accountNumber);
        // pageable filter
        Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);

        log.info("Transaction history fetch successfully by admin | accountNumber: {} | adminId: {} ", accountNumber, adminId);
        return transactions.map(t -> TransactionMapper.toTransactionResponse(t, accountNumber));
    }
}
