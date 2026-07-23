package com.adnan.bankapp.notification.consumer;

import com.adnan.bankapp.notification.service.EmailService;
import com.bankapp.common.event.OtpGeneratedEvent;
import com.bankapp.common.event.RegisterSuccessfulEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "otp.generated")
    public void sendOtpNotification(OtpGeneratedEvent event){
        emailService.sendOtpMail(event);
    }

    @KafkaListener(topics = "register.successful")
    public void registerSuccessful(RegisterSuccessfulEvent event){
        emailService.sendRegisterSuccessfulMail(event);
    }

}
