package com.adnan.bankapp.authentication.repository;

import com.adnan.bankapp.authentication.entity.UserCredentials;
import com.adnan.bankapp.authentication.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserCredentials, Long> {

    public boolean existsByEmail(String email);

    public boolean existsByEmailAndStatus(String email, UserStatus status);

    public Optional<UserCredentials> getByEmailAndStatus(String email, UserStatus status);

}
