package com.adnan.bankapp.authentication.entity;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.adnan.bankapp.authentication.enums.Role;
import com.adnan.bankapp.authentication.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public UserCredentials(RegisterRequest request){
        this.email = request.email();
    }

}
