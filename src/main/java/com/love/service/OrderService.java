package com.love.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.love.model.OrderDetail;
import com.love.model.ResultInfo;

/**
 * :业务接口类
 * 
 * @author generator
 */
public interface OrderService {

    /**
     * 新增
     * 
     * 
     * @param params jsonstring from entity
     * @return code and msg
     */
    ResultInfo add(JSONObject params);


    /**
     * 查询
     * 
     * 
     * @param order jsonstring to entity
     * @return code and entity json string
     */
    OrderDetail query(OrderDetail order);


    /**
     * 分页查询
     * 
     * 
     * @param page pageInfo
     * @param order search criteria jsonstring to entity
     * @return code and dataList
     */
    ResultInfo queryPage(Page<OrderDetail> page, OrderDetail order);


    /**
     * 更新
     * 
     * @param order jsonstring to entity
     * @return code and msg
     */
    ResultInfo update(OrderDetail order);


    /**
     * 删除
     * 
     * @param params jsonstring from delete criteria
     * @return code and msg
     */
    ResultInfo delete(JSONObject params);


    ResultInfo getAllCash(JSONObject params);
}
