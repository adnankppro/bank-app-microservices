package com.adnan.bankapp.authentication.configuration;

import com.adnan.bankapp.authentication.dto.RegisterRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {


    @Bean
    public RedisTemplate<String, RegisterRequest> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, RegisterRequest> redis = new RedisTemplate<>();
        redis.setConnectionFactory(redisConnectionFactory);
        redis.setKeySerializer(new StringRedisSerializer());
        redis.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redis.afterPropertiesSet();
        return redis;
    }

}
