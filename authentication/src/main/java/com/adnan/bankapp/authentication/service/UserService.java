package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.entity.Credentials;
import com.adnan.bankapp.authentication.exception.InvalidAccountStatusException;
import com.adnan.bankapp.authentication.kafka.producer.UserRejectedEventProducer;
import com.adnan.bankapp.authentication.kafka.producer.UserApprovedEventProducer;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.bankapp.common.enums.UserStatus;
import com.bankapp.common.exception.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserApprovedEventProducer userApprovedEventProducer;
    private final UserRejectedEventProducer userRejectedEventProducer;

    @Transactional
    public void approve(Long id) {
        log.info("User approval request received | userId: {}", id);

        Credentials user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User approval failed | reason: user not found | userId: {}", id);
            return new UserIdNotFoundException("User not found");
        });

        UserStatus status = user.getStatus();
        if(status == UserStatus.ACTIVE || status == UserStatus.PENDING_PASSWORD_GENERATION){
            log.warn("User approval failed | reason: account is already approved | userId: {}", id);
            throw new InvalidAccountStatusException("Your account is already approved");
        }

        if (status != UserStatus.PENDING_ADMIN_VERIFICATION){
            log.warn("User approval failed | reason: invalid account status | userID: {}", id);
            throw new InvalidAccountStatusException("Invalid account status");
        }

        user.setStatus(UserStatus.PENDING_PASSWORD_GENERATION);
        userApprovedEventProducer.publish(user.getEmail(), id);

        log.info("User approval request completed successfully | userId: {}", id);

    }

    @Transactional
    public void reject(Long id) {
        log.info("User rejection request received | userId: {}", id);

        Credentials user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User rejection failed | reason: user not found | userId: {}", id);
            return new UserIdNotFoundException("User ID not found");
        });

        if (user.getStatus() != UserStatus.PENDING_ADMIN_VERIFICATION){
            log.warn("User failed | reason: invalid account status | userID: {}", id);
            throw new InvalidAccountStatusException("Invalid account status");
        }

        user.setStatus(UserStatus.REJECTED);
        log.info("User rejected successfully | userId: {}", id);

        userRejectedEventProducer.publish(user.getEmail(), id);
    }
}
