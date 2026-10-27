package com.adnan.bankapp.profile.repository;

import com.adnan.bankapp.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {

    boolean existsByEmailOrId( String email, Long id);

    Optional<String> getNameById(Long id);
}
