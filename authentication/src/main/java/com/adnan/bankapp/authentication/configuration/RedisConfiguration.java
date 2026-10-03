package com.adnan.bankapp.authentication.configuration;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {


    @Bean
    public RedisTemplate<String, RegisterRequest> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper){
        RedisTemplate<String, RegisterRequest> redis = new RedisTemplate<>();
        redis.setConnectionFactory(redisConnectionFactory);
        redis.setKeySerializer(new StringRedisSerializer());
        redis.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, RegisterRequest.class));
        redis.afterPropertiesSet();
        return redis;
    }

}
