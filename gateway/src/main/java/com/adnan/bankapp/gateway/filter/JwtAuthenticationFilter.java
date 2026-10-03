package com.adnan.bankapp.gateway.filter;

import com.adnan.bankapp.gateway.dto.JwtUserDetails;
import com.adnan.bankapp.gateway.service.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtService jwtService;
    private final List<String> publicPaths = List.of("/auth/login", "/auth/register", "/auth/register/verify","/auth/password/setup","/auth/password/setup/verify", "/auth/refresh");


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        System.out.println(path);
        boolean isPublicPath = publicPaths.stream()
                .anyMatch(path::startsWith);

        if (isPublicPath){
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            JwtUserDetails userDetails = jwtService.getUserDetails(token);
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", String.valueOf(userDetails.id()))
                    .header("X-User-Email", userDetails.email())
                    .header("X-User-Role", userDetails.role())
                    .build();
            exchange = exchange.mutate().request(mutatedRequest).build();
        }catch (JwtException e){
            log.warn("Exception: {}", e.getMessage(), e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } catch (Exception e){
            log.warn("Exception occurred: {}", e.getMessage(), e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
