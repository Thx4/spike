package com.donggugu.spike.common;

import com.donggugu.spike.common.utils.RedisPoolUtil;
import com.donggugu.spike.common.stockwithredis.RedisKeysConstant;
import com.donggugu.spike.pojo.Stock;
import com.donggugu.spike.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * cache pre heat
 * start after spring boot run
 */
@Component
public class RedisPreheatRunner implements ApplicationRunner {

    @Autowired
    private StockService stockService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // search sale well commodity
        Stock stock = stockService.getStockById(1);

        //delete old cache
        RedisPoolUtil.del(RedisKeysConstant.STOCK_COUNT + Optional.ofNullable( stock.getCount()));
        RedisPoolUtil.del(RedisKeysConstant.STOCK_SALE + Optional.ofNullable(stock.getSale()));
        RedisPoolUtil.del(RedisKeysConstant.STOCK_VERSION + Optional.ofNullable(stock.getVersion()));

        //cache pre heat
        int sid = stock.getId();
        RedisPoolUtil.set(RedisKeysConstant.STOCK_COUNT + sid, String.valueOf(Optional.ofNullable( stock.getCount())));
        RedisPoolUtil.set(RedisKeysConstant.STOCK_SALE + sid, String.valueOf(Optional.ofNullable(stock.getSale())));
        RedisPoolUtil.set(RedisKeysConstant.STOCK_VERSION + sid, String.valueOf(Optional.ofNullable(stock.getVersion())));
    }

}
