package com.donggugu.spike.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisPool {

    private static JedisPool pool;

    @Value("${redis.maxTotal}")
    private static Integer maxTotal = 300;

    @Value("${redis.maxIdle}")
    private static Integer maxIdle = 100;

    @Value("${redis.maxWait}")
    private static Integer maxWait = 10000;

    @Value("${redis.testOnBorrow}")
    private static Boolean testOnBorrow = true;

    @Value("${spring.redis.host}")
    private static String redisIP;

    @Value("${spring.redis.port}")
    private static Integer redisPort;

    @Value("${spring.redis.password}")
    private static String password;

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setBlockWhenExhausted(true);
        config.setMaxWaitMillis(maxWait);

        pool = new JedisPool(config, redisIP, redisPort, 1000 * 2, password);
    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void jedisPoolClose(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
