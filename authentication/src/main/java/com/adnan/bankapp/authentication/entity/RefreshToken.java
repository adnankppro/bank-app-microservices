package com.adnan.bankapp.authentication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenId;

    @Column(nullable = false)
    private String token;

    private Instant createdAt;

    private Instant expiresAt;

    private boolean revoked;

}
