package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.entity.Credentials;
import com.adnan.bankapp.authentication.entity.RefreshToken;
import com.adnan.bankapp.authentication.exception.UserIdNotFoundException;
import com.adnan.bankapp.authentication.repository.RefreshTokenRepository;
import com.adnan.bankapp.authentication.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(Long id, HttpServletResponse response) {
        log.info("Creating refresh token | userId: {}", id);
        Credentials userCredentials = userRepository.findById(id).orElseThrow(() ->{
          log.warn("Refresh token creation failed | reason: userId not found | userId: {}", id);
          return new UserIdNotFoundException("User ID not found");
        });

        Instant now = Instant.now();
        String tokenId = UUID.randomUUID().toString();
        String rawToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenId(tokenId);
        refreshToken.setToken(passwordEncoder.encode(rawToken));
        refreshToken.setCreatedAt(now);
        refreshToken.setExpiresAt(now.plus(14, ChronoUnit.DAYS));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", rawToken+"."+tokenId)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(Duration.ofDays(14))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
