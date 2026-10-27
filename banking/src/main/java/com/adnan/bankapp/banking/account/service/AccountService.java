package com.adnan.bankapp.banking.account.service;


import com.adnan.bankapp.banking.account.enums.AccountStatus;
import com.adnan.bankapp.banking.account.dto.*;
import com.adnan.bankapp.banking.account.entity.AccountType;
import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.account.repository.AccountTypeRepository;
import com.adnan.bankapp.banking.exception.IncorrectPinException;
import com.adnan.bankapp.banking.exception.InvalidAccountNumberException;
import com.adnan.bankapp.banking.account.mapper.AccountMapper;
import com.adnan.bankapp.banking.account.repository.AccountRepository;
import com.adnan.bankapp.banking.account.util.AccountNumberGenerator;
import com.adnan.bankapp.banking.feign.service.BranchClientService;
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
import org.springframework.transaction.annotation.Propagation;
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
    private final AccountTypeRepository accountTypeRepository;
    private final BranchClientService branchClientService;

    @Transactional
    public void create(CreateBankAccountRequest request) {
        log.info("Request for new bank account creation received | userId: {} | accountTypeId: {} | branchCode: {}", request.userId(), request.accountTypeId(), request.branchCode());

        if(accountRepository.existsByUserIdAndAccountTypeId(request.userId(),  request.accountTypeId())){
            log.warn("Bank account creation failed | reason: User already have a bank account with requested account type | userId: {} | accountTypeId: {} | branchCode: {}", request.userId(), request.accountTypeId(), request.branchCode());
            throw new InvalidRequestException("You already have a bank account with requested account type.");
        }

        AccountType accountType = accountTypeRepository.findById(request.accountTypeId()).orElseThrow(() -> new InvalidRequestException("Invalid account type"));
        branchClientService.exists(request.branchCode()); // exception will be thrown if branchCode is invalid.

        String accountNumber = accountNumberGenerator.generate();

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountType(accountType);
        bankAccount.setUserId(request.userId());
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setAccountHolderName(request.accountHolderName());
        bankAccount.setBranchCode(request.branchCode());
        bankAccount.setStatus(AccountStatus.PENDING_PIN_GENERATION);

        BankAccount newAccount = accountRepository.save(bankAccount);

        log.info("Bank account created successfully | userId: {} | accountTypeId: {} | branchCode: {} | accountNumber: {}", newAccount.getUserId(), newAccount.getAccountType().getId(), newAccount.getBranchCode(), newAccount.getAccountNumber());
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
    public void changePin(ChangePinRequest request, Long userId) {
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

    @Transactional
    public BankAccount getBankAccountByAccountNumberAndUserIdForUpdate(String accountNumber, Long userId) {
        log.info("Request received to fetch bank account for update | accountNumber: {} | userId: {}", accountNumber, userId);
        BankAccount bankAccount = accountRepository.findByAccountNumberAndUserIdForUpdate(accountNumber, userId).orElseThrow(() ->{
            log.warn("Bank account fetch failed | reason: invalid account number | accountNumber: {} | userId: {}", accountNumber, userId);
            return new InvalidAccountNumberException("Invalid account number");
        });
        log.info("Bank account fetch successfully | accountNumber: {} | userId: {}", accountNumber, userId);
        return bankAccount;
    }

    @Transactional
    public BankAccount getBankAccountByAccountNumberForUpdate( String accountNumber) {
        log.info("Request received to fetch bank account for update | accountNumber: {}", accountNumber);
        BankAccount bankAccount = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() ->{
            log.warn("Bank account fetch failed | reason: invalid account number | accountNumber: {}", accountNumber);
            return new InvalidAccountNumberException("Invalid account number");
        });
        log.info("Bank account fetch successfully | accountNumber: {}", accountNumber);
        return bankAccount;}

    @Transactional
    public AccountTypeDetails createNewAccountType(CreateAccountTypeRequest request) {
        log.info("Request received to create to new account type | accountTypeName: {}", request.accountTypeName());

        if (accountTypeRepository.existsByAccountTypeName(request.accountTypeName())){
            log.warn("New account type creation failed | reason: duplicate accountTypeName | accountTypeName: {}", request.accountTypeName());
            throw new InvalidRequestException("Duplicate accountTypeName");
        }

        AccountType accountType = accountMapper.toAccountType(request);
        accountType = accountTypeRepository.save(accountType);

        return accountMapper.toAccountTypeDetails(accountType);
    }


    public boolean existsByAccountNumberAndUserId(String accountNumber, Long userId) {
        return accountRepository.existsByAccountNumberAndUserId(accountNumber, userId);
    }


    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    public VerifyAccountHolderResponse verifyAccountHolder(String accountNumber) {
        String name = accountRepository.getAccountHolderNameByAccountNumber(accountNumber).orElseThrow(() -> new InvalidAccountNumberException("Invalid account number"));
        return new VerifyAccountHolderResponse(name);
    }

    public void blockAccount(BankAccount bankAccount) {
        log.info("Request received to block account | accountNumber: {}", bankAccount.getAccountNumber());
        bankAccount.setStatus(AccountStatus.BLOCKED);
        log.info("Account blocked successfully | accountNumber: {}", bankAccount.getAccountNumber());
    }
}
