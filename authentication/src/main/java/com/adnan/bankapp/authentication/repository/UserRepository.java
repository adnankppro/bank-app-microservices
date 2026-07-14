package com.adnan.bankapp.authentication.repository;

import com.adnan.bankapp.authentication.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserCredentials, Long> {

    public boolean existsByEmail(String email);

}
