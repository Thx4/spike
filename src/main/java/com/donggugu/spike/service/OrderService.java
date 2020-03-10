package com.donggugu.spike.service;

import com.donggugu.spike.pojo.Stock;

public interface OrderService {

    int delOrderDBBefore();

    int createWrongOrder(int sid) throws Exception;

    int createOptimisticOrder(int sid) throws Exception;

    int createOrderWithLimitAndRedis(int sid) throws Exception;

    void createOrderWithLimitRedisKafka(int sid) throws Exception;

    int consumerTopicToCreateOrderWithKafka(Stock stock) throws Exception;
}
