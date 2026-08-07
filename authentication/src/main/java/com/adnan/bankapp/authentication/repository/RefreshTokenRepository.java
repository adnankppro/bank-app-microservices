package com.adnan.bankapp.authentication.repository;

import com.adnan.bankapp.authentication.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("""
    select r from RefreshToken r join fetch r.user u where r.tokenId = :tokenId and r.expiresAt > :now and r.revoked = false
""")
    Optional<RefreshToken> findValidByTokenId(@Param("tokenId") String tokenId,@Param("now") Instant now);
}
