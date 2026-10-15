package com.adnan.bankapp.authentication.kafka.producer;

import com.bankapp.common.event.UserApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserApprovedEventProducer {

    private final KafkaTemplate<String, UserApprovedEvent> kafkaTemplate;

    public void publish(String email, Long id){
        kafkaTemplate.send("user.approved", new UserApprovedEvent(email, id));
    }

}
