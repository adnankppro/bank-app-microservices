package com.adnan.bankapp.authentication.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("BRANCH")
public interface BranchFeignClient {

    @GetMapping("/branches/exists/{branchCode}")
    public void exists(@PathVariable String branchCode);

}
