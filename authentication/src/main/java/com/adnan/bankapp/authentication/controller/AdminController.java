package com.adnan.bankapp.authentication.controller;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.dto.RegisterResponse;
import com.adnan.bankapp.authentication.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<RegisterResponse> create(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.create(request));
    }

}
