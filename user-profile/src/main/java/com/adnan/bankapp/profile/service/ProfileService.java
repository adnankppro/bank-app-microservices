package com.adnan.bankapp.profile.service;

import com.adnan.bankapp.profile.dto.ProfileFilter;
import com.adnan.bankapp.profile.dto.UpdateProfileRequest;
import com.adnan.bankapp.profile.entity.Profile;
import com.adnan.bankapp.profile.exception.ProfileNotFoundException;
import com.adnan.bankapp.profile.mapper.ProfileMapper;
import com.adnan.bankapp.profile.repository.ProfileRepository;
import com.adnan.bankapp.profile.specification.ProfileSpecification;
import com.bankapp.common.dto.CreateProfileRequest;
import com.bankapp.common.dto.ProfileResponse;
import com.bankapp.common.enums.UserStatus;
import com.bankapp.common.exception.ProfileAlreadyExistsException;
import com.bankapp.common.exception.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Transactional(readOnly = true)
    public Page<ProfileResponse> getAll(Pageable pageable, ProfileFilter filter) {
        log.info("Fetch all profile request received | page: {} | size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Specification<Profile> profileSpecification = ProfileSpecification.build(filter);
        Page<Profile> profiles = repository.findAll(profileSpecification, pageable);
        log.info("Profiles fetched successfully");
        return profiles.map(profileMapper::toResponse);
    }

    @Transactional
    public ProfileResponse update(Long id, UpdateProfileRequest request) {
        log.info("Update request received | id: {}", id);
        Profile profile = repository.findById(id).orElseThrow(() -> {
            log.error("Update failed | reason: profile not found | id: {}", id);
            return new ProfileNotFoundException("Profile not found!");
        });
        profileMapper.updateUser(profile, request);
        log.info("Update completed successfully | id: {}", id);
        return profileMapper.toResponse(profile);
    }

    @Transactional
    public void changeStatus(Long id, UserStatus status){
        log.info("Changing account status | userId: {} | status: {}", id, status);
        Profile user = repository.findById(id).orElseThrow(() -> {
            log.warn("Account status change failed | reason: user ID not found | userId: {} | status: {}", id, status);
            return new UserIdNotFoundException("User ID not found");
        });
        user.setStatus(status);
        log.info("Account status changed successfully | userId: {} | status: {}", id, status);
    }

    @Transactional(readOnly = true)
    public String getNameById(Long id) {
        log.info("Request received to fetch user's name | userId: {}", id);
        return repository.getNameById(id).orElseThrow(() -> {
            log.warn("Account status change failed | reason: user ID not found | userId: {}", id);
            return new UserIdNotFoundException("User ID not found");
        });
    }
}
