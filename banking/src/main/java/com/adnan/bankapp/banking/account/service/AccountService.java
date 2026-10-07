package com.adnan.bankapp.banking.account.service;


import com.adnan.bankapp.banking.account.AccountStatus;
import com.adnan.bankapp.banking.account.dto.*;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.exception.IncorrectPinException;
import com.adnan.bankapp.banking.account.exception.InvalidAccountNumberException;
import com.adnan.bankapp.banking.account.mapper.AccountMapper;
import com.adnan.bankapp.banking.account.repository.AccountRepository;
import com.adnan.bankapp.banking.account.util.AccountNumberGenerator;
import com.adnan.bankapp.banking.util.CommonAccountUtil;
import com.bankapp.common.dto.CreateBankAccountRequest;
import com.bankapp.common.exception.InvalidRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountMapper accountMapper;
    private final CommonAccountUtil commonAccountUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(CreateBankAccountRequest request) {
        log.info("Request for new bank account creation received | userId: {} | accountType: {} | branchCode: {}", request.userId(), request.bankAccountType(), request.branchCode());

        if(accountRepository.existsByUserIdAndAccountType(request.userId(), request.bankAccountType())){
            log.warn("Bank account creation failed | reason: User already have a bank account with requested account type | userId: {} | accountType: {} | branchCode: {}", request.userId(), request.bankAccountType(), request.branchCode());
            throw new InvalidRequestException("You already have a bank account with requested account type.");
        }

        String accountNumber = accountNumberGenerator.generate();

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountType(request.bankAccountType());
        bankAccount.setUserId(request.userId());
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setBranchCode(request.branchCode());
        bankAccount.setStatus(AccountStatus.PENDING_PIN_GENERATION);

        BankAccount newAccount = accountRepository.save(bankAccount);

        log.info("Bank account created successfully | userId: {} | accountType: {} | branchCode: {} | accountId: {}", newAccount.getUserId(), newAccount.getAccountType(), newAccount.getBranchCode(), newAccount.getId());
    }

    @Transactional(readOnly = true)
    public Page<AccountDetailsResponse> getAllAccounts(Pageable pageable) {
        log.info("Request received to fetch all account details | pageNumber: {} | pageSize: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BankAccount> bankAccounts = accountRepository.findAll(pageable);
        log.info("Account details fetched successfully | pageNumber: {} | pageSize: {}", pageable.getPageNumber(), pageable.getPageSize());
        return bankAccounts.map(accountMapper::toAccountDetailsResponse);
    }

    @Transactional(readOnly = true)
    public AccountDetailsResponse getAccountDetailsByAccountNumber(String accountNumber) {
        log.info("Request received to fetch account details | accountNumber: {}", accountNumber);
        BankAccount bankAccount = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() ->{
            log.warn("Account details not found | accountNumber: {}", accountNumber);
            return new InvalidAccountNumberException("Invalid account number");
        });
        return accountMapper.toAccountDetailsResponse(bankAccount);
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String accountNumber, Long userId) {
        log.info("Request received to fetch balance | userId: {} | accountNumber: {}", userId, accountNumber);

        BankAccount bankAccount = accountRepository.findByAccountNumberAndUserId(accountNumber, userId).orElseThrow(() ->{
            log.warn("Balance fetch failed | reason: invalid account number | userId: {} | accountNumber: {}", userId, accountNumber);
            return new InvalidRequestException("Invalid account number");
        });

        log.info("Balance fetched successfully | userId: {} | accountNumber: {}", userId, accountNumber);
        return new BalanceResponse(bankAccount.getAccountNumber(), bankAccount.getBalance());
    }


    @Transactional
    public void setPin(@Valid PinGenerationRequest request, Long userId) {
        log.info("Request received to set PIN | userid: {} | accountNumber: {}", userId, request.accountNumber());

        if (!request.pin().equals(request.confirmPin())){
            log.warn("PIN set up failed | reason: entered PINs are different | userId: {} | accountNumber: {}", userId, request.accountNumber());
            throw new IncorrectPinException("Entered PINs are different. Please re‑enter.");
        }

        BankAccount bankAccount = accountRepository.findByAccountNumberAndUserId(request.accountNumber(), userId).orElseThrow(() ->{
            log.warn("PIN set up failed | reason: invalid account number | userId: {} | accountNumber: {}", userId, request.accountNumber());
            return new InvalidRequestException("Invalid account number");
        });

        if (bankAccount.getStatus() != AccountStatus.PENDING_PIN_GENERATION){
            log.warn("PIN set up failed | reason: invalid account status | userId: {} | accountNumber: {}", userId, request.accountNumber());
            throw new InvalidRequestException("Invalid account status.");
        }

        bankAccount.setPin(passwordEncoder.encode(request.pin()));
        bankAccount.setStatus(AccountStatus.ACTIVE);

        log.info("PIN set up completed successfully | userId: {} | accountNumber: {}", userId, request.accountNumber());
    }

    @Transactional
    public void changePin(@Valid ChangePinRequest request, Long userId) {
        log.info("Request received to change PIN | userid: {} | accountNumber: {}", userId, request.accountNumber());

        if (!request.newPin().equals(request.confirmPin())){
            log.warn("PIN changing failed | reason: entered PINs are different | userId: {} | accountNumber: {}", userId, request.accountNumber());
            throw new IncorrectPinException("Entered PINs are different. Please re‑enter.");
        }

        BankAccount bankAccount = accountRepository.findByAccountNumberAndUserId(request.accountNumber(), userId).orElseThrow(() ->{
            log.warn("PIN changing failed | reason: invalid account number | userId: {} | accountNumber: {}", userId, request.accountNumber());
            return new InvalidRequestException("Invalid account number");
        });

        if (bankAccount.getStatus() != AccountStatus.ACTIVE){
            log.warn("PIN changing failed | reason: account is not active | userId: {} | accountNumber: {}", userId, request.accountNumber());
            throw new InvalidRequestException("Your account is not active. Please contact branch");
        }

        commonAccountUtil.validatePin(bankAccount, request.oldPin());// validate old pin

        bankAccount.setPin(passwordEncoder.encode(request.newPin()));

        log.info("PIN changed successfully | userId: {} | accountNumber: {}", userId, request.accountNumber());
    }
}
