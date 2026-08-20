package com.adnan.bankapp.authentication.feign;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("BRANCH-SERVICE")
public interface BranchFeignClient {

    @GetMapping("/branches/exists/{branchCode}")
    public void exists(@PathVariable String branchCode);

}
