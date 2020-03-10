package com.donggugu.spike.service.impl;

import com.donggugu.spike.common.utils.RedisPoolUtil;
import com.donggugu.spike.service.OrderService;
import com.donggugu.spike.common.stockwithredis.RedisKeysConstant;
import com.donggugu.spike.common.stockwithredis.StockWithRedis;
import com.donggugu.spike.dao.StockOrderMapper;
import com.donggugu.spike.pojo.Stock;
import com.donggugu.spike.pojo.StockOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service(value = "OrderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StockOrderMapper orderMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("SPIKE")
    private String kafkaTopic;

    private Gson gson = new GsonBuilder().create();

    @Override
    public int delOrderDBBefore() {
        return orderMapper.delOrderDBBefore();
    }

    @Override
    public int createWrongOrder(int sid) throws Exception {
        Stock stock = checkStock(sid);
        saleStock(stock);
        int res = createOrder(stock);
        return res;
    }

    @Override
    public int createOptimisticOrder(int sid) throws Exception {
        //check stock
        Stock stock = checkStock(sid);
        //sale stock by optimistic
        saleStockOptimistic(stock);
        //create order
        int id = createOrder(stock);
        return id;
    }

    @Override
    public int createOrderWithLimitAndRedis(int sid) throws Exception {
        //check stock by redis
        Stock stock = checkStockWithRedis(sid);
        //sale stock by optimistic redis
        saleStockOptimsticWithRedis(stock);
        int res = createOrder(stock);
        return res;

    }

    @Override
    public void createOrderWithLimitRedisKafka(int sid) throws Exception {
        //check stock
        Stock stock = checkStockWithRedis(sid);
        //send order to kafka ,need serialize stock
        kafkaTemplate.send(kafkaTopic, gson.toJson(stock));
        log.info("send to kafka success");

    }

    @Override
    public int consumerTopicToCreateOrderWithKafka(Stock stock) throws Exception {
        //sale stock by optimistic redis
        saleStockOptimsticWithRedis(stock);
        int res = createOrder(stock);

        if (res == 1) {
            log.info("kafka consume topic create order success");
        } else {
            log.info("kafka consume topic create order failed");
        }
        return res;
    }


    /**
     * Redis check stock
     *  @param sid
     * @return
     * @throws Exception
     */
    private Stock checkStockWithRedis(int sid) throws Exception {
        Integer count = Integer.parseInt(RedisPoolUtil.get(RedisKeysConstant.STOCK_COUNT + sid));
        Integer sale = Integer.parseInt(RedisPoolUtil.get(RedisKeysConstant.STOCK_SALE + sid));
        Integer version = Integer.parseInt(RedisPoolUtil.get(RedisKeysConstant.STOCK_VERSION + sid));

        if (count < 1) {
            log.info("stock not enough");
            throw new RuntimeException("stock not enough, Redis current count: " + sale);
        }

        Stock stock = new Stock();
        stock.setId(sid);
        stock.setCount(count);
        stock.setSale(sale);
        stock.setVersion(version);
        stock.setName("commodity");

        return stock;
    }

    /**
     * update database
     * @param stock
     * @throws Exception
     */
    private void saleStockOptimsticWithRedis(Stock stock) throws Exception {
        int res = stockService.updateStockByOptimistic(stock);
        if (res == 0) {
            throw new RuntimeException("Concurrent update stock error");
        }
        //update redis
        StockWithRedis.updateStockWithRedis(stock);

    }

    /**
     * check stock
     * @param sid
     * @return
     * @throws Exception
     */
    private Stock checkStock(int sid) throws Exception {
        Stock stock = stockService.getStockById(sid);
        if (stock.getCount() < 1) {
            throw new RuntimeException("stock not enough");
        }
        return stock;
    }

    /**
     * sale
     * @param stock
     * @return
     */
    private int saleStock(Stock stock) {
        stock.setSale(stock.getSale()+1);
        stock.setCount(stock.getCount()-1);
        return stockService.updateStockById(stock);
    }


    /**
     * sale by optimistic
     * @param stock
     * @throws Exception
     */
    private void saleStockOptimistic(Stock stock) throws Exception {
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0) {
            throw new RuntimeException("Concurrent update stock error");
        }
    }

    /**
     * @param stock
     * @return
     * @throws Exception
     */
    private int createOrder(Stock stock) throws Exception {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        order.setCreateTime(new Date());
        int res = orderMapper.insertSelective(order);
        if (res == 0) {
            throw new RuntimeException("create order error");
        }
        return res;
    }
}
