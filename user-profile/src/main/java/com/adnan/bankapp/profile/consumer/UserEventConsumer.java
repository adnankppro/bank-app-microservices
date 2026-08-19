package com.adnan.bankapp.profile.consumer;

import com.adnan.bankapp.profile.service.ProfileService;
import com.bankapp.common.enums.UserStatus;
import com.bankapp.common.event.UserApplicationRejectedEvent;
import com.bankapp.common.event.UserApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ProfileService profileService;

    @KafkaListener(topics = "user.approved")
    public void userApproved(UserApprovedEvent event){
        profileService.changeStatus(event.id(), UserStatus.PENDING_PASSWORD_GENERATION);
    }

    @KafkaListener(topics = "user.rejected")
    public void userRejected(UserApplicationRejectedEvent event){
        profileService.changeStatus(event.id(), UserStatus.REJECTED);
    }

}
