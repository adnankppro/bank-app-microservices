package com.adnan.bankapp.authentication.controller;

import com.adnan.bankapp.authentication.dto.*;
import com.adnan.bankapp.authentication.service.AuthenticationService;
import com.bankapp.common.dto.MyDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Void> verifyRegister(@Valid @RequestBody VerifyRegisterRequest request){
        service.verifyRegister(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/setup")
    public ResponseEntity<Void> setupPassword(@RequestBody PasswordSetupRequest request){
        service.generatePasswordOtp(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/setup/verify")
    public ResponseEntity<Void> verifyPasswordSetup(@RequestBody VerifyPasswordSetupRequest request){
        service.setPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<RefreshResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response){
        return ResponseEntity.ok(service.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken){
        return ResponseEntity.ok(service.refresh(refreshToken));
    }

    @PostMapping("/password/change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal MyDetails myDetails, @Valid @RequestBody PasswordChangeRequest request){
        service.changePassword(request, myDetails.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String cookieToken, HttpServletResponse response){
        service.logout(cookieToken, response);
        return ResponseEntity.noContent().build();
    }

}
