package com.adnan.bankapp.authentication.client;

import com.adnan.bankapp.authentication.feign.ProfileFeignClient;
import com.bankapp.common.dto.CreateProfileRequest;
import com.bankapp.common.exception.ServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceClient {

    private final ProfileFeignClient profileFeignClient;

    @Retry(name = "default", fallbackMethod = "createFallback")
    @CircuitBreaker(name = "default", fallbackMethod = "createFallback")
    public void create(CreateProfileRequest request){
        profileFeignClient.create(request);
    }

    public void createFallback(CreateProfileRequest request, Exception e){
        log.error("Profile service unavailable | email: {} | error: {}", request.email(), e.getMessage());
        throw new ServiceException("Service temporarily unavailable. Please try again later.");
    }
}
