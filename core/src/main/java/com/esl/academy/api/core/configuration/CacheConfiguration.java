package com.esl.academy.api.core.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static com.esl.academy.api.core.constants.CacheId.AUTH_TOKEN;
import static com.esl.academy.api.core.constants.CacheId.AUTH_USER;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;

@Configuration
@RequiredArgsConstructor
public class CacheConfiguration {

    private final ObjectMapper objectMapper;

    @Value("${application.jwt.access.expiration}")
    private long accessExpiration;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(cacheObjectMapper, Object.class);
        cacheObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        cacheObjectMapper.activateDefaultTyping(cacheObjectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, PROPERTY);

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        // Define TTL per cache
        return (builder) -> builder
                .withCacheConfiguration(AUTH_USER.getCacheName(),
                        defaultCacheConfig.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(AUTH_TOKEN.getCacheName(),
                        defaultCacheConfig.entryTtl(Duration.ofMinutes(accessExpiration)));
    }
}
