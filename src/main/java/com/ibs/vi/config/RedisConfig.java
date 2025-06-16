/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ibs.vi.config;

import com.ibs.vi.model.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *
 * @author jithin123
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Route> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Route> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Keys- Strings
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // Values- Route objects, use Kryo for Route.class
        KryoRedisSerializer<Route> kryoSerializer = new KryoRedisSerializer<>(Route.class);

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(kryoSerializer);
        template.setHashValueSerializer(kryoSerializer);

        template.afterPropertiesSet();
        return template;
    }


}

