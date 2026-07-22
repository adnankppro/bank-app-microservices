package com.adnan.bankapp.branch.dto;

public record BranchResponse(String branchName,
                             String branchCode,
                             String city,
                             String state,
                             String address,
                             String ifscCode) {
}
