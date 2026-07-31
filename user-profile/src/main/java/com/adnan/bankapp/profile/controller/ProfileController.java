package com.adnan.bankapp.profile.controller;


import com.adnan.bankapp.profile.dto.ProfileResponse;
import com.adnan.bankapp.profile.service.ProfileService;
import com.bankapp.common.dto.CreateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService service;

    @PostMapping
    public ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request){
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }

}
