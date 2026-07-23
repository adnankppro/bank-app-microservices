package com.adnan.bankapp.otp.producer;

import com.bankapp.common.event.OtpGeneratedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpEventProducer {

    private final KafkaTemplate<String, OtpGeneratedEvent> kafkaTemplate;

    public void publish(OtpGeneratedEvent event){
        kafkaTemplate.send("otp.generated", event);
    }
}
