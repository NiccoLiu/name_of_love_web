package com.love.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.love.model.Sign;

/**
 * DAO类
 * 
 * @author generator
 */
public interface SignDAO extends BaseMapper<Sign> {

    /**
     * 分页查询
     * 
     * @param page page info
     * @param sign search criteria
     * @param orderByField order's field
     * @param isAsc true is asc ,or not
     * @return list
     */
    List<Sign> getSignPage(@Param("page") Page<Sign> page, @Param("sign") Sign sign,
            @Param("orderByField") String orderByField, @Param("isAsc") boolean isAsc);

    Sign findSign(@Param("sign") Sign sign);
}
