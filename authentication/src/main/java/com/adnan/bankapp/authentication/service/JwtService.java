package com.adnan.bankapp.authentication.service;

import com.adnan.bankapp.authentication.dto.JwtUserDetails;
import com.adnan.bankapp.authentication.enums.Role;
import com.adnan.bankapp.authentication.security.MyUserDetails;
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
    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    public String generate(MyUserDetails myUserDetails) {
        return Jwts.builder()
                .subject(myUserDetails.getUsername())
                .claim("id", myUserDetails.getId())
                .claim("role", myUserDetails.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public JwtUserDetails getUserDetails(String token) {
        Claims claims = extractClaims(token);

        Long id = claims.get("id", Long.class);
        String role = claims.get("role", String.class);

        if (id == null || role == null) {
            throw new IllegalStateException("Missing required claims in JWT");
        }

        return new JwtUserDetails(id, claims.getSubject(), Role.valueOf(role));
    }

}
