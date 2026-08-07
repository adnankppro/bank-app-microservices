package com.adnan.bankapp.authentication.repository;

import com.adnan.bankapp.authentication.entity.Credentials;
import com.bankapp.common.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Credentials, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndStatus(String email, UserStatus status);

    Optional<Credentials> getByEmail(String email);

    Optional<Credentials> getByEmailAndStatus(String email, UserStatus status);

}
