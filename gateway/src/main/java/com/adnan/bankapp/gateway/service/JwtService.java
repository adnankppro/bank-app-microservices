package com.adnan.bankapp.gateway.service;

import com.adnan.bankapp.gateway.dto.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtUserDetails getUserDetails(String token) {
        Claims claims = extractClaims(token);

        Long id = claims.get("id", Long.class);
        String role = claims.get("role", String.class);

        if (id == null || role == null) {
            throw new IllegalStateException("Missing required claims in JWT");
        }

        return new JwtUserDetails(id, claims.getSubject(), role);
    }

}
