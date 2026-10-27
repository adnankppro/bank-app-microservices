package com.adnan.bankapp.profile.entity;

import com.bankapp.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Profile {

    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;

    private String address;

    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private String branchCode;

    private UserStatus status;

}
