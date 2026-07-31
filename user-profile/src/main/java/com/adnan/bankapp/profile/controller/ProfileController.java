package com.adnan.bankapp.profile.controller;


import com.adnan.bankapp.profile.dto.ProfileResponse;
import com.adnan.bankapp.profile.service.ProfileService;
import com.bankapp.common.dto.CreateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService service;

    @PostMapping
    public ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request){
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(service.create(request));
    }


}
