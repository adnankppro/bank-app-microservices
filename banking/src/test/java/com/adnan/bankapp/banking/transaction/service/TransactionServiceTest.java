package com.adnan.bankapp.banking.transaction.service;


import com.adnan.bankapp.banking.account.service.AccountService;
import com.adnan.bankapp.banking.transaction.dto.TransactionRequest;
import com.adnan.bankapp.banking.transaction.enums.TransactionPurpose;
import com.adnan.bankapp.banking.transaction.repository.TransactionRepository;
import com.adnan.bankapp.banking.util.CommonAccountUtil;
import com.bankapp.common.dto.MyDetails;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CommonAccountUtil commonAccountUtil;

    @InjectMocks
    private TransactionService transactionService;


    @Nested
    public class sendTests{

        @Test
        public void shouldThrowInvalidRequestExceptionWhenSenderAndReceiverAccountNumbersAreSame(){

            TransactionRequest request = new TransactionRequest("0123456789", "0123456789", "1234", BigDecimal.valueOf(100), TransactionPurpose.PERSONAL);
            MyDetails userDetails = new MyDetails("adnankppro@gmail.com", 1L);



        }




    }
}
