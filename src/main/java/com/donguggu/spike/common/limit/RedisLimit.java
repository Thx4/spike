package com.donguggu.spike.common.limit;

import com.donguggu.spike.common.utils.RedisPool;
import com.donguggu.spike.common.utils.ScriptUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Collections;

@Slf4j
public class RedisLimit {
    private static final int FAIL_CODE = 0;

    private static Integer limit = 5;

    public static Boolean limit() {
        Jedis jedis = null;
        Object result = null;

        try {
            jedis = RedisPool.getJedis();

            String script = ScriptUtil.getScript("limit.lua");

            // request limit
            String key = String.valueOf(System.currentTimeMillis() / 1000);

            // count limit
            result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));

            if (FAIL_CODE != (Long) result) {
                log.info("get token success");
                return true;
            }
        } catch (Exception e) {
            log.error("limit get Jedis error:", e);
        }finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return false;
    }
}
