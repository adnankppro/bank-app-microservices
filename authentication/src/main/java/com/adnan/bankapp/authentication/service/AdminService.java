package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.dto.RegisterResponse;
import com.adnan.bankapp.authentication.entity.Credentials;
import com.adnan.bankapp.authentication.enums.Role;
import com.adnan.bankapp.authentication.exception.EmailAlreadyExistsException;
import com.adnan.bankapp.authentication.feign.ProfileFeignClient;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.bankapp.common.dto.CreateProfileRequest;
import com.bankapp.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final ProfileFeignClient profileFeignClient;

    @Transactional
    public RegisterResponse create(RegisterRequest request) {
        log.info("Admin creation request received | email: {}", request.email());

        if(userRepository.existsByEmail(request.email())){
            log.warn("Admin creation failed | reason: duplicate email | email: {}", request.email());
            throw new EmailAlreadyExistsException("Email already used.");
        }

        Credentials admin = new Credentials(request.email());
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.PENDING_PASSWORD_GENERATION);

        admin = userRepository.save(admin);
        profileFeignClient.create(new CreateProfileRequest(admin.getId(), request.name(), admin.getEmail(), request.address(), request.dateOfBirth(), request.branchCode(), admin.getStatus()));

        log.info("Admin creation completed successfully | email: {}", admin.getEmail());

        return new RegisterResponse(request.email(), "Registration completed successfully.");
    }
}
