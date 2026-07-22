package com.adnan.bankapp.branch.repository;

import com.adnan.bankapp.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {

    public boolean existsByBranchCode(String branchCode);

    public Optional<Branch> getByBranchCode(String branchCode);

}
