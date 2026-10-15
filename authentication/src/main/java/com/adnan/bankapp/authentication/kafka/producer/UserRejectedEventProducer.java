package com.adnan.bankapp.authentication.kafka.producer;

import com.bankapp.common.event.UserApplicationRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRejectedEventProducer {

    private final KafkaTemplate<String, UserApplicationRejectedEvent> kafkaTemplate;

    public void publish(String email, Long id){
        kafkaTemplate.send("user.rejected", new UserApplicationRejectedEvent(email, id));
    }

}
