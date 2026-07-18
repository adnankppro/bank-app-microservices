package com.adnan.bankapp.authentication.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient
public interface OtpServiceClient {

    public void generate()

}
