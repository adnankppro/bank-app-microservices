package com.adnan.bankapp.profile.controller;


import com.adnan.bankapp.profile.dto.ProfileFilter;
import com.adnan.bankapp.profile.dto.ProfileResponse;
import com.adnan.bankapp.profile.service.ProfileService;
import com.bankapp.common.dto.CreateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
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

    // get details (self)

    // get all (specification) create ProfileResponseFilter
    public ResponseEntity<Page<ProfileResponse>> getAll(@PageableDefault Pageable pageable, @ModelAttribute ProfileFilter filter){
        return ResponseEntity.ok(service.getAll(pageable, filter));
    }

    // update (self)

}
