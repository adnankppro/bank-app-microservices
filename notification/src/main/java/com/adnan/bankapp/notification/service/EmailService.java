package com.adnan.bankapp.notification.service;

import com.bankapp.common.event.OtpGeneratedEvent;
import com.bankapp.common.event.RegisterSuccessfulEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;

    public void sendOtpMail(OtpGeneratedEvent event) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(event.email());
            helper.setSubject("OTP for Password Generation");
            helper.setText(emailTemplateService.otpMail(event), true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRegisterSuccessfulMail(RegisterSuccessfulEvent event) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(event.email());
            helper.setSubject("Register Successful");
            helper.setText(emailTemplateService.registerSuccessful(event), true);
            javaMailSender.send(message);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
