package com.adnan.bankapp.authentication.feign.client;

import com.bankapp.common.dto.CreateProfileRequest;
import com.bankapp.common.dto.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("profile-service")
public interface ProfileFeignClient {

    @PostMapping("/profiles")
    public void create(CreateProfileRequest request);

    @GetMapping("/profiles/{id}")
    public ProfileResponse getById(@PathVariable Long id);

}
