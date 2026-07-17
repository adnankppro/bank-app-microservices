package com.adnan.bankapp.authentication.configuration;

import com.bankapp.common.feign.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }

}
