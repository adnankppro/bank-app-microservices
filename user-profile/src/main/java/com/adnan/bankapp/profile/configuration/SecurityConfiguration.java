package com.adnan.bankapp.profile.configuration;

import com.bankapp.common.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,"/profiles/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/profiles/me").authenticated()
                        .requestMatchers(HttpMethod.POST,"/profiles").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET,"/profiles", "/profiles/{id}", "/profiles/{id}/name").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(gatewayFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public GatewayFilter gatewayFilter(){
        return new GatewayFilter();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Return an empty service so Spring doesn’t create the default user
        return username -> {
            throw new UsernameNotFoundException("No user lookup here — JWT only");
        };
    }

}
