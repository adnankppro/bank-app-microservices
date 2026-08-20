package com.adnan.bankapp.authentication.client;

import com.adnan.bankapp.authentication.feign.BranchFeignClient;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BranchServiceClient {

    private final BranchFeignClient branchFeignClient;

    @Retry(name = "default", fallbackMethod = "existsFallback")
    public void exists(String branchCode){
        branchFeignClient.exists(branchCode);
    }

    public void existsFallback(String branchCode, Exception e){
        log.error("Branch service unavailable | error: {}", e.getMessage());
    }
}
