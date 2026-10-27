package com.adnan.bankapp.banking.kafka.consumer;

import com.adnan.bankapp.banking.account.service.AccountService;
import com.bankapp.common.dto.CreateBankAccountRequest;
import com.bankapp.common.event.CreateBankAccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankAccountConsumer {

    private final AccountService accountService;

    @KafkaListener(topics = "create.bank.account")
    private void createBankAccount(CreateBankAccountEvent event){
        accountService.create(new CreateBankAccountRequest(event.userId(), event.accountHolderName(), event.accountTypeId(), event.branchCode()));
    }

}
