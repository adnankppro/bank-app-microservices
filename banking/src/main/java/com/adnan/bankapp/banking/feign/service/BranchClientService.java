package com.adnan.bankapp.banking.feign.service;

import com.adnan.bankapp.banking.feign.client.BranchFeignClient;
import com.bankapp.common.exception.ServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BranchClientService {

    private final BranchFeignClient branchFeignClient;

    @Retry(name = "default", fallbackMethod = "existsFallback")
    @CircuitBreaker(name = "default", fallbackMethod = "existsFallback")
    public void exists(String branchCode){
        branchFeignClient.exists(branchCode);
    }

    public void existsFallback(String branchCode, Exception e){
        log.error("Branch service unavailable | error: {}", e.getMessage());
        throw new ServiceException("Service temporarily unavailable. Please try again later.");
    }
}
