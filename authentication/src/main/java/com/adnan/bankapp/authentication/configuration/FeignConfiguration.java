package com.adnan.bankapp.authentication.configuration;

import com.bankapp.common.feign.BankAppFeignClientConfig;
import com.bankapp.common.feign.CustomErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor(HttpServletRequest request){
        BankAppFeignClientConfig bankAppFeignClientConfig = new BankAppFeignClientConfig();
        return bankAppFeignClientConfig.requestInterceptor(request);
    }

}
