package com.adnan.bankapp.branch.controller;

import com.adnan.bankapp.branch.dto.BranchResponse;
import com.adnan.bankapp.branch.dto.CreateBranchRequest;
import com.adnan.bankapp.branch.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/branches")
public class BranchController {

    private final BranchService service;

    @PostMapping
    public ResponseEntity<BranchResponse> create(@Valid @RequestBody CreateBranchRequest request){
        return ResponseEntity.status(201).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<BranchResponse>> getAll(@PageableDefault Pageable pageable){
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{branchCode}")
    public ResponseEntity<BranchResponse> getByBranchCode(@PathVariable String branchCode){
        return ResponseEntity.ok(service.getByBranchCode(branchCode));
    }

    @GetMapping("/exists/{branchCode}")
    public void exists(@PathVariable String branchCode){
        service.exists(branchCode);
    }

}
