package com.adnan.bankapp.authentication.controller;

import com.adnan.bankapp.authentication.dto.ApiResponse;
import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.dto.RegisterResponse;
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
        log.info("Register request received for email: {}", request.email());
        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Registered successfully", "ok", LocalDateTime.now()));
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}
