package com.adnan.bankapp.notification.service;

import com.bankapp.common.event.OtpGeneratedEvent;
import com.bankapp.common.event.RegisterSuccessfulEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    public String otpMail(OtpGeneratedEvent event){
        Context context = new Context();
        context.setVariable("otp", event.otp());
        return templateEngine.process("otp", context);
    }

    public String registerSuccessful(RegisterSuccessfulEvent event) {
        Context context = new Context();
        context.setVariable("name", event.name());
        return templateEngine.process("register", context);
    }
}
