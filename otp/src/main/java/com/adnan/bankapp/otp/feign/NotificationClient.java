package com.adnan.bankapp.otp.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("NOTIFICATION-SERVICE")
public interface NotificationClient {

    public void sendOtp();

}
