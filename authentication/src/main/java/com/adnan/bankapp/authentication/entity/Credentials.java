package com.adnan.bankapp.authentication.entity;


import com.adnan.bankapp.authentication.enums.Role;
import com.bankapp.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Credentials {

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

    public Credentials(String email){
        this.email = email;
    }

}
