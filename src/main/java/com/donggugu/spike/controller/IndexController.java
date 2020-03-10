package com.donggugu.spike.controller;


import com.donggugu.spike.common.limit.RedisLimit;
import com.donggugu.spike.common.stockwithredis.StockWithRedis;
import com.donggugu.spike.service.OrderService;
import com.donggugu.spike.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author donggugu
 */
@Slf4j
@RestController
@RequestMapping(value = "/")
public class IndexController {
    private static final String success = "SUCCESS";
    private static final String error = "ERROR";

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockService stockService;


    /**
     * init db cache before test
     * @return
     */
    @PostMapping("initDBAndRedis")
    public String initDBAndRedisBefore() {
        int res = 0;
        try {
            //init stock
            res = stockService.initDBBefore();
            //del order table
            res &= (orderService.delOrderDBBefore() == 0 ? 1 : 0);
            //reset cache
            StockWithRedis.initRedisBefore();
        } catch (Exception e) {
            log.error("Exception ",e);
        }

        if (res == 1) {
            log.info("reset DB and cache success");
        }
        return res == 1 ? success : error;
    }

    /**
     * spike basic logic
     * with oversold problem
     * @param sid
     * @return
     */
    @PostMapping("createWrongOrder")
    public String createWrongOrder(int sid) {
        int res = 0;
        try {
            res = orderService.createWrongOrder(sid);

        } catch (Exception e) {
            log.error("Exception: ",e);
        }
        return res == 1 ? success : error;
    }

    /**
     * sale stock optimistic
     * @param sid
     * @return
     */
    @PostMapping("createOptimisticOrder")
    public String createOptimisticOrder(int sid) {
        int res = 0;
        try {
            res = orderService.createOptimisticOrder(sid);
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }

    /**
     * optimistic +  limit
     * @param sid
     *
     * @return
     */
    @PostMapping("createOptimisticLimitOrder")
    public String createOptimisticLimitOrder(int sid) {
        int res = 0;
        try {
            if (RedisLimit.limit()) {
                res = orderService.createOptimisticOrder(sid);
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }

    /**
     * Redis cache
     * preheat,need stock.id = 1
     * @param sid
     * @return
     */
    @PostMapping("createOrderWithLimitAndRedis")
    public String createOrderWithLimitAndRedis(int sid) {
        int res = 0;
        try {
            if (RedisLimit.limit()) {
                res = orderService.createOrderWithLimitAndRedis(sid);
                if (res == 1) {
                    log.info("秒杀成功");
                }
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }


    @PostMapping("createOrderWithLimitAndRedisAndKafka")
    public String createOrderWithLimitAndRedisAndKafka(int sid) {
        try {
            if (RedisLimit.limit()) {
                orderService.createOrderWithLimitRedisKafka(sid);
            }
        } catch (Exception e) {
            log.error("Exception ",e);
        }
        return "秒杀请求正在处理，排队中";
    }







}
