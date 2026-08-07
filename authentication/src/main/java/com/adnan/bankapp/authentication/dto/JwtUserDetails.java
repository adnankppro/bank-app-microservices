package com.adnan.bankapp.authentication.dto;

import com.adnan.bankapp.authentication.enums.Role;

public record JwtUserDetails (Long id,
                              String email,
                              Role role){
}
