package com.adnan.bankapp.branch.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBranchRequest(@NotBlank String branchName,
                                  @NotBlank String branchCode,
                                  @NotBlank String city,
                                  @NotBlank String state,
                                  @NotBlank String address,
                                  @NotBlank String ifscCode) {
}
