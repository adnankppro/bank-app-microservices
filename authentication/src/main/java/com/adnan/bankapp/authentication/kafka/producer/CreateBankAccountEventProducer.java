package com.adnan.bankapp.authentication.kafka.producer;

import com.bankapp.common.event.CreateBankAccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateBankAccountEventProducer {

    private final KafkaTemplate<String, CreateBankAccountEvent> kafkaTemplate;

    public void publish(CreateBankAccountEvent createBankAccountEvent){
        kafkaTemplate.send("create.bank.account", createBankAccountEvent);
    }

}
