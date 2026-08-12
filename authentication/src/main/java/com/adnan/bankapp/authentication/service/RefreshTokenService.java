package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.entity.Credentials;
import com.adnan.bankapp.authentication.entity.RefreshToken;
import com.adnan.bankapp.authentication.exception.InvalidRefreshTokenException;
import com.adnan.bankapp.authentication.exception.UserIdNotFoundException;
import com.adnan.bankapp.authentication.repository.RefreshTokenRepository;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.adnan.bankapp.authentication.security.MyUserDetails;
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
        refreshToken.setUser(userCredentials);
        refreshToken.setTokenId(tokenId);
        refreshToken.setToken(passwordEncoder.encode(rawToken));
        refreshToken.setCreatedAt(now);
        refreshToken.setExpiresAt(now.plus(14, ChronoUnit.DAYS));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        ResponseCookie cookie = setRefreshCookie(rawToken+"."+tokenId);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("Refresh token created successfully | userId: {}", id);
    }

    public MyUserDetails verify(String cookieToken) {
        if(cookieToken == null){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String[] tokenArray = cookieToken.split("\\.");
        if (tokenArray.length != 2){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        Instant now = Instant.now();
        String token = tokenArray[0];
        String tokenId = tokenArray[1];

        RefreshToken refreshToken = refreshTokenRepository.findValidByTokenId(tokenId, now).orElseThrow(() ->
                new InvalidRefreshTokenException("Invalid refresh token"));

        if (!passwordEncoder.matches(token, refreshToken.getToken())){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        return new MyUserDetails(refreshToken.getUser());
    }

    @Transactional
    public void revokeAllTokenByUserId(Long id) {
        refreshTokenRepository.revokeActiveByUserId(id);
    }

    public ResponseCookie setRefreshCookie(String token){
        return ResponseCookie.from("refreshToken",token)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(Duration.ofDays(14))
                .build();
    }

    public Long getUserIdFromToken(String cookieToken) {
        if(cookieToken == null){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String[] tokenArray = cookieToken.split("\\.");
        if (tokenArray.length != 2){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String tokenId = tokenArray[1];
        return refreshTokenRepository.getUserIdByTokenId(tokenId).orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
    }
}
