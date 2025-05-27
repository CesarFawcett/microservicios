package edu.unimag.product.service.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer; 

@Configuration 
@EnableCaching 
public class CacheConfig {

    public static final String PRODUCT_CACHE ="products"; 

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer())) 
            .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())); 

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put(PRODUCT_CACHE, defaultCacheConfig);

        // Construye el RedisCacheManager
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultCacheConfig) 
            .withInitialCacheConfigurations(redisCacheConfigurationMap) 
            .build();
    }
}
