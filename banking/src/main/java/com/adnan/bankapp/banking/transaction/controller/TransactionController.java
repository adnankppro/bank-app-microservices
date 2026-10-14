package com.adnan.bankapp.banking.transaction.controller;

import com.adnan.bankapp.banking.transaction.dto.*;
import com.adnan.bankapp.banking.transaction.service.TransactionService;
import com.bankapp.common.dto.MyDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // send (user)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponse> sendMoney(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal MyDetails myDetails){
        return ResponseEntity.ok(transactionService.send(request, myDetails));
    }

    // deposit
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<DepositResponse> deposit(@Valid @RequestBody DepositRequest request, @AuthenticationPrincipal MyDetails adminDetails){
        return ResponseEntity.ok(transactionService.deposit(request, adminDetails));
    }

    // transaction history (for admin and user separately)

    //user
    @GetMapping("/user/{accountNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TransactionResponse>> getMyTransactions(@PathVariable String accountNumber, @AuthenticationPrincipal MyDetails myDetails, @ModelAttribute TransactionFilter filter, @PageableDefault Pageable pageable){
        return ResponseEntity.ok(transactionService.getTransactionHistoryForUser(accountNumber, myDetails.id(), filter, pageable));
    }

    @GetMapping("/admin/{accountNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsHistoryForAdmin(@PathVariable String accountNumber, @AuthenticationPrincipal MyDetails myDetails, @ModelAttribute TransactionFilter filter, @PageableDefault Pageable pageable){
        return ResponseEntity.ok(transactionService.getTransactionHistoryForAdmin(accountNumber, myDetails.id(), filter, pageable));
    }

}
