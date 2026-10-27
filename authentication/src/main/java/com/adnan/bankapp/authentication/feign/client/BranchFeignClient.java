package com.adnan.bankapp.authentication.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("BRANCH-SERVICE")
public interface BranchFeignClient {

    @GetMapping("/branches/exists/{branchCode}")
    void exists(@PathVariable String branchCode);

}
