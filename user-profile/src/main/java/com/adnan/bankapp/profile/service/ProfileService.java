package com.adnan.bankapp.profile.service;

import com.adnan.bankapp.profile.dto.ProfileResponse;
import com.adnan.bankapp.profile.entity.Profile;
import com.adnan.bankapp.profile.exception.ProfileNotFoundException;
import com.adnan.bankapp.profile.mapper.ProfileMapper;
import com.adnan.bankapp.profile.repository.ProfileRepository;
import com.bankapp.common.dto.CreateProfileRequest;
import com.bankapp.common.exception.ProfileAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository repository;
    private final ProfileMapper profileMapper;

    @Transactional
    public ProfileResponse create(CreateProfileRequest request) {
        log.info("Create request received | email: {} | id: {}", request.email(), request.id());
        if(repository.existsByEmailOrId(request.email(), request.id())){
            log.error("Create request failed | A profile with given ID or Email already exists | email: {} | id: {}", request.email(), request.id());
            throw new ProfileAlreadyExistsException("profile with given ID or Email already exists");
        }
        Profile profile = repository.save(profileMapper.toProfile(request));
        log.info("Profile created successfully | email: {} | id: {}", profile.getEmail(), profile.getId());
        return profileMapper.toResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getById(Long id) {
        log.info("Profile fetch request received | id: {}", id);
        return profileMapper.toResponse(repository.findById(id).orElseThrow(() -> {
            log.warn("Profile not found | id: {}", id);
            return new ProfileNotFoundException("Profile not found | id: "+id);
        }));
    }
}
