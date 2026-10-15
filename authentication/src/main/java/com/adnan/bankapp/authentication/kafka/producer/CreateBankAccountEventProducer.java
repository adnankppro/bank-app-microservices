package com.adnan.bankapp.authentication.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateBankAccountEventProducer {

    private final KafkaTemplate<String, CreateBankA>

}
