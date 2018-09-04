package com.zemcho.pe.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置
 *
 * @Author Jetvin
 * @Date 2018/8/29
 * @Time 17:13
 * @Version ╮(╯▽╰)╭
 * <p>
 * <!--         ░░░░░░░░░░░░░░░░░░░░░░░░▄░░░        -->
 * <!--         ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░░        -->
 * <!--         ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐ ░        -->
 * <!--         ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░        -->
 * <!--         ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐ ░        -->
 * <!--         ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░        -->
 * <!--         ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒ ░        -->
 * <!--         ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░        -->
 * <!--         ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄░        -->
 * <!--         -░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒        -->
 * <!--         ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒░        -->
 * <!--                                             -->
 * <!--                 咦！有人在改BUG               -->
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@EnableCaching
@Getter
@Setter
public class RedisConfig {

    private RedisProperties ehall;

    private RedisProperties write;

    private RedisProperties read;

    @Value(value = "${spring.redis.lettuce.pool.max-active}")
    private Integer maxActive;

    @Value(value = "${spring.redis.lettuce.pool.max-wait}")
    private Long maxWait;

    @Value(value = "${spring.redis.lettuce.pool.max-idle}")
    private Integer maxIdle;

    @Value(value = "${spring.redis.lettuce.pool.min-idle}")
    private Integer minIdle;

    @Bean("genericObjectPoolConfig")
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);

        System.out.println(genericObjectPoolConfig.toString());
        return genericObjectPoolConfig;
    }

    /* TODO 用于连接从大厅备份过来的登录信息的redis */
    @Bean(name = "ehallLettuceConnectionFactory")
    public LettuceConnectionFactory ehallLettuceConnectionFactory(@Qualifier("genericObjectPoolConfig") GenericObjectPoolConfig genericObjectPoolConfig) {

        return basicLettuceConnectionFactory(genericObjectPoolConfig, ehall);
    }

    @Bean("ehallStringRedisTemplate")
    public RedisTemplate<String, String> ehallStringRedisTemplate(@Qualifier("ehallLettuceConnectionFactory") LettuceConnectionFactory factory){
        RedisTemplate<String, String> stringRedisTemplate = new RedisTemplate<>();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());

        return stringRedisTemplate;
    }


    /* TODO 用于连接系统自身的写redis */
    @Bean(name = "writeLettuceConnectionFactory")
    public LettuceConnectionFactory writeLettuceConnectionFactory(@Qualifier("genericObjectPoolConfig") GenericObjectPoolConfig genericObjectPoolConfig) {
        return basicLettuceConnectionFactory(genericObjectPoolConfig, write);
    }

    @Bean("writeObjectRedisTemplate")
    public RedisTemplate<String, Object> writeObjectRedisTemplate(@Qualifier("writeLettuceConnectionFactory") LettuceConnectionFactory factory) {

        return basicObjectRedisTemplate(factory);
    }

    @Bean("writeIntegerRedisTemplate")
    public RedisTemplate<String, Integer> writeIntegerRedisTemplate(@Qualifier("writeLettuceConnectionFactory") LettuceConnectionFactory factory) {

        return basicIntegerRedisTemplate(factory);
    }


    /* TODO 用于连接系统自身的读redis */
    @Bean(name = "readLettuceConnectionFactory")
    public LettuceConnectionFactory readLettuceConnectionFactory(@Qualifier("genericObjectPoolConfig") GenericObjectPoolConfig genericObjectPoolConfig) {
        return basicLettuceConnectionFactory(genericObjectPoolConfig, read);
    }

    @Bean("readObjectRedisTemplate")
    public RedisTemplate<String, Object> readObjectRedisTemplate(@Qualifier("readLettuceConnectionFactory") LettuceConnectionFactory factory) {

        return basicObjectRedisTemplate(factory);
    }

    @Bean("readIntegerRedisTemplate")
    public RedisTemplate<String, Integer> readIntegerRedisTemplate(@Qualifier("readLettuceConnectionFactory") LettuceConnectionFactory factory) {

        return basicIntegerRedisTemplate(factory);
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("writeLettuceConnectionFactory") LettuceConnectionFactory factory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(cacheConfig).build();
    }

    /* TODO 用于连接系统自身的读redis */
    @Bean("redisTemplate")
    public RedisTemplate<String, String> redisTemplate(@Qualifier("writeLettuceConnectionFactory") LettuceConnectionFactory factory){
        RedisTemplate stringRedisTemplate = new RedisTemplate<>();
        stringRedisTemplate.setConnectionFactory(factory);

        return stringRedisTemplate;
    }

    private LettuceConnectionFactory basicLettuceConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig, RedisProperties properties){
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(properties.getHost());
        standaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        standaloneConfiguration.setPort(properties.getPort());
        standaloneConfiguration.setDatabase(properties.getDatabase());

        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(properties.getTimeout()))
                .poolConfig(genericObjectPoolConfig)
                .build();

        return new LettuceConnectionFactory(standaloneConfiguration, clientConfiguration);
    }

    private RedisTemplate<String, Object> basicObjectRedisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> objectRedisTemplate = new RedisTemplate<>();
        objectRedisTemplate.setConnectionFactory(factory);
        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return objectRedisTemplate;
    }

    private RedisTemplate<String, Integer> basicIntegerRedisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Integer> integerRedisTemplate = new RedisTemplate<>();
        integerRedisTemplate.setConnectionFactory(factory);
        integerRedisTemplate.setKeySerializer(new StringRedisSerializer());
        integerRedisTemplate.setValueSerializer(new GenericToStringSerializer<>(Integer.class));

        return integerRedisTemplate;
    }
}
