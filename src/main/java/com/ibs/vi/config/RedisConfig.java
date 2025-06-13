/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ibs.vi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 * @author jithin123
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Kryo for key and value serialization
        KryoRedisSerializer<Object> kryoSerializer = new KryoRedisSerializer<>(Object.class);
        template.setKeySerializer(kryoSerializer);
        template.setValueSerializer(kryoSerializer);
        template.setHashKeySerializer(kryoSerializer);
        template.setHashValueSerializer(kryoSerializer);

        template.afterPropertiesSet();
        return template;
    }
    
}
