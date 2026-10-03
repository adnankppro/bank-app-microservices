package com.adnan.bankapp.profile.entity;

import com.bankapp.common.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Profile {

    @Id
    private Long id;

    private String name;

    private String email;

    private String address;

    private LocalDate dateOfBirth;

    private String branch;

    private UserStatus status;

}
