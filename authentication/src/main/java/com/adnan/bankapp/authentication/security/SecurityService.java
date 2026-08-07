package com.adnan.bankapp.authentication.security;

import com.adnan.bankapp.authentication.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    public MyUserDetails validate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        return (MyUserDetails) authentication.getPrincipal();
    }

}
