package com.example.socialnetwork.common.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineConfig {
    @Value("${jwt.access-token.expiration-time}")
    private int accessTokenExpirationTime;

    @Value("${otp.expiration-time}")
    private int otpExpirationTime;

    @Bean
    @Primary
    CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("invalidated_tokens");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(accessTokenExpirationTime, TimeUnit.SECONDS)
                .maximumSize(10000));
        return cacheManager;
    }

    @Bean
    Cache<String, String> otpCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(otpExpirationTime, TimeUnit.SECONDS)
                .maximumSize(1000)
                .build();
    }
}
