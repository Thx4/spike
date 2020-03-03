package com.donguggu.spike.service;

import com.donguggu.spike.pojo.Stock;

public interface StockService {

    int getStockCount(int id);

    Stock getStockById(int id);

    int updateStockById(Stock stock);

    int updateStockByOptimistic(Stock stock);

    int initDBBefore();
}
