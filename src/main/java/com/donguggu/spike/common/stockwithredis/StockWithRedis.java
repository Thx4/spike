package com.donguggu.spike.common.stockwithredis;

import com.donguggu.spike.common.utils.RedisPool;
import com.donguggu.spike.common.utils.RedisPoolUtil;
import com.donguggu.spike.pojo.Stock;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

@Slf4j
public class StockWithRedis {

    /**
     * Redis transaction to ensure update
     * when catch exception should del cache
     * @param stock
     */
    public static void updateStockWithRedis(Stock stock) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();

            //start transaction
            Transaction transaction = jedis.multi();

            //transaction operation
            RedisPoolUtil.decr(RedisKeysConstant.STOCK_COUNT + stock.getId());
            RedisPoolUtil.incr(RedisKeysConstant.STOCK_SALE + stock.getId());
            RedisPoolUtil.incr(RedisKeysConstant.STOCK_VERSION + stock.getId());

            //transaction over
            List<Object> list = transaction.exec();
        } catch (Exception e) {
            log.error("update Stock get Jedis examples error: ",e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
    }

    /**
     * initial cache
     */
    public static void initRedisBefore() {
        Jedis jedis = null;

        try {
            jedis = RedisPool.getJedis();

            //start transaction
            Transaction transaction = jedis.multi();

            //transaction operation
            RedisPoolUtil.set(RedisKeysConstant.STOCK_COUNT + 1, "50");
            RedisPoolUtil.set(RedisKeysConstant.STOCK_SALE + 1, "0");
            RedisPoolUtil.set(RedisKeysConstant.STOCK_VERSION + 1, "0");

            //over
            List<Object> list = transaction.exec();
        } catch (Exception e) {
            log.error("initRedis get Jedis error: ",e);
        }finally {
            RedisPool.jedisPoolClose(jedis);
        }
    }


}
