package com.love.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.love.dto.GridDTO;
import com.love.mapper.OrderDAO;
import com.love.mapper.UserDAO;
import com.love.model.OrderDetail;
import com.love.model.ResultInfo;
import com.love.model.User;
import com.love.service.OrderService;
import com.love.service.RedisService;

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
    @Autowired
    private UserDAO userService;
    @Autowired
    private RedisService redisService;

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
    public ResultInfo queryPage(Page<OrderDetail> page, OrderDetail orderDetail) {
        logger.debug("queryPage OrderDetail ,the entity is {},the page is {}", orderDetail, page);
        ResultInfo resultInfo = new ResultInfo(0, "success");
        List<OrderDetail> result = orderDAO.getOrderDetailPage(page, orderDetail,
                page.getOrderByField(), page.isAsc());
        GridDTO<OrderDetail> grid = new GridDTO<OrderDetail>();
        grid.setTotalRecord(page.getTotal());
        grid.setTotalPage(page.getPages());
        grid.setList(result);
        resultInfo.setData(grid);
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

    @Override
    public ResultInfo getAllCash(JSONObject params) {
        ResultInfo resultInfo = new ResultInfo(0, "success");
        logger.debug("getAllCash by params {}", params);
        String sessionKey = params.getString("sessionKey");
        String openId = redisService.get(sessionKey);
        double money = orderDAO.getAllCash(openId);
        User user = new User();
        user.setOpenid(openId);
        user = userService.selectOne(user);
        Map<String, Object> map = new HashMap<>(2);
        map.put("money", money);
        map.put("balance", user.getBalance());
        resultInfo.setData(map);
        return resultInfo;
    }

}
