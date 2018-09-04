package com.zemcho.pe.config.redis;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RedisProperties {

    private String host;

    private Integer port;

    private String password;

    private Long timeout;

    private Integer database;
}
