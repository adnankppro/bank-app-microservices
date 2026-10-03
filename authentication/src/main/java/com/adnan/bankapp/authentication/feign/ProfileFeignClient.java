package com.adnan.bankapp.authentication.feign;

import com.bankapp.common.dto.CreateProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("profile-service")
public interface ProfileFeignClient {

    @PostMapping("/profiles")
    public void create(CreateProfileRequest request);

}
