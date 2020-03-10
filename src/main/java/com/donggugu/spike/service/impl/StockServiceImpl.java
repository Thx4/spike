package com.donggugu.spike.service.impl;

import com.donggugu.spike.dao.StockMapper;
import com.donggugu.spike.pojo.Stock;
import com.donggugu.spike.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "StockService")
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Override
    public int getStockCount(int id) {
        return stockMapper.selectByPrimaryKey(id).getCount();
    }

    @Override
    public Stock getStockById(int id) {
        return stockMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateStockById(Stock stock) {
        return stockMapper.updateByPrimaryKeySelective(stock);
    }

    @Override
    public int updateStockByOptimistic(Stock stock) {
        return stockMapper.updateByOptimistic(stock);
    }

    @Override
    public int initDBBefore() {
        return stockMapper.initDBBefore();
    }
}
