package com.love.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.love.model.OrderDetail;
import com.love.model.ResultInfo;
import com.love.service.OrderService;
import com.love.util.PageFactory;

/**
 * : Controller类
 * 
 * @author generator
 */
@RestController
@RequestMapping(value = "/order")
public class OrderController {


    @Autowired
    private OrderService orderServiceImpl;


    /**
     * 新增
     * 
     * 
     * @param params jsonstring
     * @return code and msg
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo add(@RequestBody JSONObject params) {
        return orderServiceImpl.add(params);
    }


    /**
     * 查询
     * 
     * 
     * @param params jsonstring
     * @return code and entity json string
     */
    @PostMapping("query")
    @ResponseBody
    public ResultInfo query(@RequestBody JSONObject params) {
        ResultInfo resultInfo = new ResultInfo(0, "success");
        OrderDetail paramEntity = params.toJavaObject(OrderDetail.class);
        OrderDetail order = orderServiceImpl.query(paramEntity);
        resultInfo.setData(order);
        return resultInfo;
    }


    /**
     * 分页查询
     * 
     * 
     * @param params pageInfo and search criteria
     * @return code and dataList
     */
    @PostMapping("queryPage")
    @ResponseBody
    public ResultInfo queryPage(@RequestBody JSONObject params) {
        ResultInfo resultInfo = new ResultInfo(0, "success");
        Page<OrderDetail> page = new PageFactory<OrderDetail>().defaultPage(params);
        OrderDetail order = JSONObject.parseObject(params.toJSONString(), OrderDetail.class);
        resultInfo = orderServiceImpl.queryPage(page, order);
        return resultInfo;
    }


    /**
     * 更新
     * 
     * 
     * @param params jsonstring
     * @return code and msg
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo update(@RequestBody JSONObject params) {
        OrderDetail order = params.toJavaObject(OrderDetail.class);
        ResultInfo resultInfo = orderServiceImpl.update(order);
        return resultInfo;
    }


    /**
     * 删除
     * 
     * 
     * @param params jsonstring
     * @return code and msg
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo delete(@RequestBody JSONObject params) {
        ResultInfo resultInfo = orderServiceImpl.delete(params);
        return resultInfo;
    }


}
