package com.adnan.bankapp.branch.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String branchName;
    @Column(unique = true)
    private String branchCode;

    private String city;

    private String state;
    @Column(unique = true)
    private String ifscCode;

    private String address;

}
