package com.adnan.bankapp.banking.account.controller;

import com.adnan.bankapp.banking.account.dto.*;
import com.adnan.bankapp.banking.account.service.AccountService;
import com.bankapp.common.dto.CreateBankAccountRequest;
import com.bankapp.common.dto.MyDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    //create -- currently used internally in only auth service after admin verification of user details
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateBankAccountRequest request){
        accountService.create(request);
        return ResponseEntity.noContent().build();
    }

    // get all (for admin)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Page<AccountDetailsResponse>> getAllAccount(@PageableDefault Pageable pageable){
        return ResponseEntity.ok(accountService.getAllAccounts(pageable));
    }

    // get by account number (admin)
    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<AccountDetailsResponse> getAccountDetailsByAccountNumber(@PathVariable @Pattern(regexp = "^\\d{10}$",
                                                                                        message = "Account number must be exactly 10 digits")
                                                                                       String accountNumber){
        return ResponseEntity.ok(accountService.getAccountDetailsByAccountNumber(accountNumber));
    }


    // check balance (self)
    @GetMapping("/{accountNumber}/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable @Pattern(regexp = "^\\d{10}$",
                                                                  message = "Account number must be exactly 10 digits")
                                                          String accountNumber,
                                                      @AuthenticationPrincipal MyDetails myDetails){
        return ResponseEntity.ok(accountService.getBalance(accountNumber, myDetails.id()));
    }

    // generate pin (self, one time per user)
    @PostMapping("/pin/setup")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> setPin(@RequestBody @Valid PinGenerationRequest request, @AuthenticationPrincipal MyDetails myDetails){
        accountService.setPin(request, myDetails.id());
        return ResponseEntity.noContent().build();
    }

    // change pin (self)
    @PatchMapping("/pin/change")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> changePin(@RequestBody @Valid ChangePinRequest request, @AuthenticationPrincipal MyDetails myDetails){
        accountService.changePin(request, myDetails.id());
        return ResponseEntity.noContent().build();
    }

}
