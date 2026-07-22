package com.adnan.bankapp.branch.mapper;

import com.adnan.bankapp.branch.dto.BranchResponse;
import com.adnan.bankapp.branch.dto.CreateBranchRequest;
import com.adnan.bankapp.branch.entity.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    BranchResponse toResponse(Branch branch);

    Branch toBranch(CreateBranchRequest request);

}
