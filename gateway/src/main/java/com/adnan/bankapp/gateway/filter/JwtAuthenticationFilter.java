package com.adnan.bankapp.gateway.filter;

import com.adnan.bankapp.gateway.dto.JwtUserDetails;
import com.adnan.bankapp.gateway.service.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtService jwtService;
    private final List<Endpoint> publicEndpoints = List.of(
            //authentication
            new Endpoint("/auth/login", HttpMethod.POST),
            new Endpoint("/auth/register", HttpMethod.POST),
            new Endpoint("/auth/register/verify", HttpMethod.POST),
            new Endpoint("/auth/password/setup", HttpMethod.POST),
            new Endpoint("/auth/password/setup/verify", HttpMethod.POST),
            new Endpoint("/auth/refresh", HttpMethod.POST),
            //branch
            new Endpoint("/branches", HttpMethod.GET),
            new Endpoint("/branches/*", HttpMethod.GET),
            new Endpoint("/branches/exists/*", HttpMethod.GET),
            //banking
            new Endpoint("/accounts/*/verify", HttpMethod.GET)
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        boolean isPublicEndpoint = publicEndpoints.stream()
                .anyMatch(e -> e.matches(path, method));

        if (isPublicEndpoint){
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
        log.info();
        return chain.filter(exchange);
    }
}

record Endpoint(String path, HttpMethod method){

    private static final AntPathMatcher antPatchMatcher = new AntPathMatcher();

    public boolean matches(String path, HttpMethod method){
        return this.method.equals(method) && antPatchMatcher.match(this.path, path);
    }
}
