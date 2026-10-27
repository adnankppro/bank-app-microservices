package com.bankapp.common.feign;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

public class BankAppFeignClientConfig {

    public RequestInterceptor requestInterceptor(HttpServletRequest request){
        return template -> {
            String role = request.getHeader("X-User-Role");
            String email = request.getHeader("X-User-Email");
            String id = request.getHeader("X-User-Id");

            if(role != null && email!=null && id != null){
                template.header("X-User-Role", role);
                template.header("X-User-Email", email);
                template.header("X-User-Id", id);
            }
        };
    }
}
