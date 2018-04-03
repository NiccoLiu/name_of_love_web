package com.love.service.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.love.mapper.OrderDAO;
import com.love.model.OrderDetail;
import com.love.model.ResultInfo;
import com.love.service.OrderService;
import com.love.util.PageFactory;
import com.love.util.PageInfoBT;

/**
 * :业务接口实现类
 * 
 * 
 * @author generator
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDAO orderDAO;

    @Override
    public ResultInfo add(JSONObject params) {
        logger.debug("add Order ,the params is {}", params);
        ResultInfo resultInfo = new ResultInfo(0, "success");
        OrderDetail order = params.toJavaObject(OrderDetail.class);
        orderDAO.insert(order);
        return resultInfo;
    }

    @Override
    public OrderDetail query(OrderDetail param) {
        logger.debug("query Order by {}", param);
        OrderDetail order = orderDAO.selectOne(param);
        return order;
    }

    @Override
    public ResultInfo queryPage(OrderDetail order) {
        logger.debug("queryPage Order ,the entity is {}", order);
        ResultInfo resultInfo = new ResultInfo(0, "success");
        Page<OrderDetail> page = new PageFactory<OrderDetail>().defaultPage();

        List<OrderDetail> result =
                orderDAO.getOrderDetailPage(page, order, page.getOrderByField(), page.isAsc());
        page.setRecords(result);
        page.setTotal(page.getTotal());
        resultInfo.setData(new PageInfoBT<>(page));
        return resultInfo;
    }


    @Override
    public ResultInfo update(OrderDetail order) {
        logger.debug("update Order ,the entity is {}", order);
        ResultInfo resultInfo = new ResultInfo(0, "success");
        orderDAO.updateById(order);
        return resultInfo;
    }

    @Override
    public ResultInfo delete(JSONObject params) {
        logger.debug("delete Order by params {}", params);
        Long id = params.getLong("id");
        ResultInfo resultInfo = new ResultInfo(0, "success");
        orderDAO.deleteById(id);
        return resultInfo;
    }

}
