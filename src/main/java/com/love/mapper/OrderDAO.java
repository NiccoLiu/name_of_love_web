package com.love.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.love.model.OrderDetail;

/**
 * DAO类
 * 
 * @author generator
 */
public interface OrderDAO extends BaseMapper<OrderDetail> {

    /**
     * 分页查询
     * 
     * @param page page info
     * @param orderDetail search criteria
     * @param orderByField order's field
     * @param isAsc true is asc ,or not
     * @return list
     */
    List<OrderDetail> getOrderDetailPage(@Param("page") Page<OrderDetail> page,
            @Param("orderDetail") OrderDetail orderDetail,
            @Param("orderByField") String orderByField, @Param("isAsc") boolean isAsc);
}
