package com.donguggu.spike.service.impl;

import com.donguggu.spike.pojo.Stock;
import com.donguggu.spike.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service(value = "OrderService")
public class OrderServiceImpl implements OrderService {
    @Override
    public int delOrderDBBefore() {
        return 0;
    }

    @Override
    public int createWrongOrder(int sid) throws Exception {
        return 0;
    }

    @Override
    public int createOptimisticOrder(int sid) throws Exception {
        return 0;
    }

    @Override
    public int createOrderWithLimitAndRedis(int sid) throws Exception {
        return 0;
    }

    @Override
    public void createOrderWithLimitRedisKafka(int sid) throws Exception {

    }

    @Override
    public int consumerTopicToCreateOrderWithKafka(Stock stock) throws Exception {
        return 0;
    }
}
