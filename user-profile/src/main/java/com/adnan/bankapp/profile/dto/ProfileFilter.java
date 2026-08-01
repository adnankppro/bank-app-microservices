package com.adnan.bankapp.profile.dto;

import com.bankapp.common.enums.UserStatus;

public record ProfileFilter(String email,
                            String branch,
                            String address,
                            String name,
                            UserStatus status) {
}
