package com.donggugu.spike.dao;

import com.donggugu.spike.pojo.StockOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockOrderMapper {
    @Insert("INSERT INTO stock_order (id,t sid, name, create_time) VALUES" +
            "(#{id}, #{sid}, #{name}, #{createTime}")
    int insertSelective(StockOrder order);


    /**
     * clear order table
     * @return
     */
    @Update("TRUNCATE TABLE stock_order")
    int delOrderDBBefore();
}
