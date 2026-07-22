package com.adnan.bankapp.branch.service;

import com.adnan.bankapp.branch.dto.BranchResponse;
import com.adnan.bankapp.branch.dto.CreateBranchRequest;
import com.adnan.bankapp.branch.entity.Branch;
import com.adnan.bankapp.branch.exception.DuplicateBranchCodeException;
import com.adnan.bankapp.branch.mapper.BranchMapper;
import com.adnan.bankapp.branch.repository.BranchRepository;
import com.bankapp.common.exception.BranchNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {

    private final BranchRepository repository;
    private final BranchMapper branchMapper;


    @Transactional(readOnly = true)
    @Cacheable(value = "branch::exists", key = "#branchCode")
    public Boolean exists(String branchCode) {
        return repository.existsByBranchCode(branchCode);
    }

    /*@Transactional(readOnly = true)
    @Cacheable(value = "branch", key = "#branchCode")*/
    public BranchResponse getByBranchCode(String branchCode) {
        log.debug("Fetching branch details | branchCode: {}", branchCode);
        return branchMapper.toResponse(repository.getByBranchCode(branchCode).orElseThrow(() ->{
            log.warn("Branch not found | branchCode: {}", branchCode);
            return new BranchNotFoundException("No branch with branch code "+branchCode);
        }));
    }

    @Transactional
    public BranchResponse create(CreateBranchRequest request) {
        log.info("Create new branch | branchCode: {}", request.branchCode());
        if(repository.existsByBranchCode(request.branchCode())){
            log.warn("Branch creation failed | branchCode: {} ", request.branchCode());
            throw new DuplicateBranchCodeException("A branch with given branch code ("+request.branchCode()+") already exists.");
        }
        Branch branch = branchMapper.toBranch(request);
        repository.save(branch);
        log.info("Branch created successfully | branchCode: {}", request.branchCode());
        return branchMapper.toResponse(branch);
    }

    @Transactional(readOnly = true)
    public Page<BranchResponse> getAll(Pageable pageable) {
        Page<Branch> branches = repository.findAll(pageable);
        return branches.map(branchMapper::toResponse);
    }
}
