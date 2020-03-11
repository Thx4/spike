package com.donggugu.spike;

import com.donggugu.spike.dao.StockMapper;
import com.donggugu.spike.dao.StockOrderMapper;
import com.donggugu.spike.pojo.Stock;
import com.donggugu.spike.pojo.StockOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
class SpikeApplicationTests {

    private Logger logger = LoggerFactory.getLogger(SpikeApplicationTests.class);

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockOrderMapper orderMapper;

    @Test
    public void StockMapperSelectByPrimaryKeyTest() {
        Stock stock = stockMapper.selectByPrimaryKey(1);
        logger.info("stock: id = {}, name = {}, count = {}, sale = {}, version = {}",
                stock.getId(), stock.getName(), stock.getCount(), stock.getSale(), stock.getVersion());
    }

    @Test
    public void StockMapperUpdateByPrimaryKeySelective() {
        Stock stock = new Stock();
        stock.setId(1);
        stock.setName("苹果手机");
        stock.setCount(11);
        stock.setSale(5);
        stock.setVersion(0);
        int res = stockMapper.updateByPrimaryKeySelective(stock);
        Assert.assertEquals(res, 1);
    }

    @Test
    public void StockMapperUpdateByOptimistic() {

        class ThreadOrder implements Runnable {
            @Override
            public void run() {
                Stock stock = new Stock();
                stock.setId(1);
                stock.setName("测试手机");
                stock.setCount(10);
                stock.setSale(5);
                stock.setVersion(0);
                int res = stockMapper.updateByOptimistic(stock);
                System.out.println(res);
                logger.info("res: " + res);
            }
        }

        // 线程池
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 15; i++) {
            service.submit(new ThreadOrder());
        }
    }

    @Test
    public void StockOrderMapperInsertSelective() {
        StockOrder order = new StockOrder();
        order.setId(2);
        order.setSid(1);
        order.setName("苹果手机");
        order.setCreateTime(new Date());
        int res = orderMapper.insertSelective(order);
        Assert.assertEquals(res, 1);
    }

}
