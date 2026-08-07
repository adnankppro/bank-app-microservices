package com.adnan.bankapp.authentication.controller;

import com.adnan.bankapp.authentication.dto.*;
import com.adnan.bankapp.authentication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication related APIs")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @Operation(summary = "Register new account", description = "Registers new user account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Register OTP send successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already used"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request){
        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Registered successfully", "ok", LocalDateTime.now()));
    }

    @PostMapping("/register/verify")
    public void verifyRegister(@Valid @RequestBody VerifyRegisterRequest request){
        service.verifyRegister(request);
    }

    @PostMapping("/password/setup")
    public void setupPassword(@RequestBody PasswordSetupRequest request){
        service.generatePasswordOtp(request);
    }

    @PostMapping("/password/setup/verify")
    public void verifyPasswordSetup(@RequestBody VerifyPasswordSetupRequest request){
        service.setPassword(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refresh")
    public void refresh(){

    }

    @PatchMapping("/password/change")
    public void changePassword(){

    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}
