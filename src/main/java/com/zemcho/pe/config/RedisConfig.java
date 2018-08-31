package com.zemcho.pe.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
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
import java.util.List;

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
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching
@Getter
@Setter
public class RedisConfig {

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
//

    @Value(value = "${spring.redis.host}")
    private String host;

    @Value(value = "${spring.redis.password}")
    private String password;

    @Value(value = "${spring.redis.port}")
    private Integer port;

    @Value(value = "${spring.redis.timeout}")
    private Long timeout;

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
        return genericObjectPoolConfig;
    }

    private LettuceConnectionFactory getLettuceConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig, Integer database){
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setDatabase(database);
        standaloneConfiguration.setHostName(host);
        standaloneConfiguration.setPassword(RedisPassword.of(password));
        standaloneConfiguration.setPort(port);

        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout))
                .poolConfig(genericObjectPoolConfig)
                .build();

        return new LettuceConnectionFactory(standaloneConfiguration, clientConfiguration);
    }

    /* TODO 用于连接从大厅备份过来的登录信息的database */
    @Bean(name = "lettuceConnectionFactory1")
    public LettuceConnectionFactory lettuceConnectionFactory1(@Qualifier("genericObjectPoolConfig") GenericObjectPoolConfig genericObjectPoolConfig) {

        return getLettuceConnectionFactory(genericObjectPoolConfig, 0);
    }

    /* TODO 用于连接系统自身的database */
    @Bean(name = "lettuceConnectionFactory2")
    public LettuceConnectionFactory lettuceConnectionFactory2(@Qualifier("genericObjectPoolConfig") GenericObjectPoolConfig genericObjectPoolConfig) {
        return getLettuceConnectionFactory(genericObjectPoolConfig, 1);
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("lettuceConnectionFactory2") LettuceConnectionFactory factory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(cacheConfig).build();
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, String> redisTemplate(@Qualifier("lettuceConnectionFactory2") LettuceConnectionFactory factory){
        RedisTemplate stringRedisTemplate = new RedisTemplate<>();
        stringRedisTemplate.setConnectionFactory(factory);

        return stringRedisTemplate;
    }

    @Bean("stringRedisTemplate")
    public RedisTemplate<String, String> stringRedisTemplate(@Qualifier("lettuceConnectionFactory1") LettuceConnectionFactory factory){
        RedisTemplate<String, String> stringRedisTemplate = new RedisTemplate<>();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());

        return stringRedisTemplate;
    }

    @Bean("objectRedisTemplate")
    public RedisTemplate<String, Object> objectRedisTemplate(@Qualifier("lettuceConnectionFactory2") LettuceConnectionFactory factory) {

        RedisTemplate<String, Object> objectRedisTemplate = new RedisTemplate<>();
        objectRedisTemplate.setConnectionFactory(factory);
        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return objectRedisTemplate;
    }

    @Bean("integerRedisTemplate")
    public RedisTemplate<String, Integer> integerRedisTemplate(@Qualifier("lettuceConnectionFactory2") LettuceConnectionFactory factory) {

        RedisTemplate<String, Integer> objectRedisTemplate = new RedisTemplate<>();
        objectRedisTemplate.setConnectionFactory(factory);
        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectRedisTemplate.setValueSerializer(new GenericToStringSerializer<>(Integer.class));

        return objectRedisTemplate;
    }
}
