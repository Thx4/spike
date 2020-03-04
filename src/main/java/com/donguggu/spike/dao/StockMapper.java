package com.donguggu.spike.dao;

import com.donguggu.spike.pojo.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockMapper {

    /**
     * initial DB
     */
    @Update("UPDATE stock SET count = 50, sale = 0, version = 0")
    int initDBBefore();

    /**
     * @param id
     * @return stock
     */
    @Select("SELECT * FROM stock WHERE id = #{id}")
    Stock selectByPrimaryKey(@Param("id") int id);

    @Update("UPDATE stock SET count = #{count}, " +
            "name = #{name}, sale = #{sale}, version = #{version}" +
            "WHERE id = #{id}")
    int updateByPrimaryKeySelective(Stock stock);

    @Update("UPDATE stock SET count = count - 1, sale = sale + 1, version = version+ 1 " +
            "WHERE id = #{id} AND version = #{version}")
    int updateByOptimistic(Stock stock);
}
