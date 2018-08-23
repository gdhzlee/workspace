package com.zemcho.pe.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.redis.cluster")
@Getter
@Setter
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {

    /* 集群 */

    private List<String> nodes;

//    @Value("${spring.redis.pool.max-idle}")
//    private int maxIdle;
//
//    @Value("${spring.redis.pool.min-idle}")
//    private int minIdle;
//
//    @Value("${spring.redis.pool.max-active}")
//    private int maxTotal;
//
//    @Bean(name = "lettuceConnectionFactory")
//    public LettuceConnectionFactory lettuceConnectionFactory(){
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(nodes);
//        return new LettuceConnectionFactory(clusterConfiguration);
//    }
//
//    @Bean(name = "poolConfig")
//    public LettucePoolConfig poolConfig(){
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxIdle(maxIdle);
//        poolConfig.setMinIdle(minIdle);
//        poolConfig.setMaxTotal(maxTotal);
//
//        return poolConfig;
//    }
//
//    @Bean("redisConnectionFactory")
//    public RedisConnectionFactory redisConnectionFactory(@Qualifier("poolConfig")JedisPoolConfig poolConfig){
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(nodes);
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration, poolConfig);
//        return jedisConnectionFactory;
//    }
//
//    @Bean("objectRedisTemplate")
//    public RedisTemplate<String,Object> objectRedisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory factory){
//
//        RedisTemplate<String, Object> objectRedisTemplate = new RedisTemplate<>();
//        objectRedisTemplate.setConnectionFactory(factory);
//        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
//        objectRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        return objectRedisTemplate;
//    }
//
//    @Bean("integerRedisTemplate")
//    public RedisTemplate<String,Integer> integerRedisTemplate(@Qualifier("lettuceConnectionFactory")LettuceConnectionFactory factory){
//
//        RedisTemplate<String, Integer> objectRedisTemplate = new RedisTemplate<>();
//        objectRedisTemplate.setConnectionFactory(factory);
//        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
//        objectRedisTemplate.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
//
//        return objectRedisTemplate;
//    }

    /* 单例 */

    @Bean("objectRedisTemplate")
    public RedisTemplate<String,Object> objectRedisTemplate(LettuceConnectionFactory factory){

        RedisTemplate<String, Object> objectRedisTemplate = new RedisTemplate<>();
        objectRedisTemplate.setConnectionFactory(factory);
        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return objectRedisTemplate;
    }

    @Bean("integerRedisTemplate")
    public RedisTemplate<String,Integer> integerRedisTemplate(LettuceConnectionFactory factory){

        RedisTemplate<String, Integer> objectRedisTemplate = new RedisTemplate<>();
        objectRedisTemplate.setConnectionFactory(factory);
        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectRedisTemplate.setValueSerializer(new GenericToStringSerializer<>(Integer.class));

        return objectRedisTemplate;
    }
}
