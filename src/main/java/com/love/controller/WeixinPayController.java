package com.love.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.love.model.ResultInfo;
import com.love.service.WeixinPayService;

/**
 * 微信支付
 *
 * @author liuxinq
 */
@RequestMapping("/wxpay")
@Controller
public class WeixinPayController {
    private static final Logger logger = LoggerFactory.getLogger(WeixinPayController.class);

    private static final String TOTALFEE_KEY = "totalAmount";

    @Autowired
    private WeixinPayService weixinPayService;

    /**
     * 统一下单，参考微信API:https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
     * 
     * @param params 请求参数
     * @return APP支付SDK需要数据
     */
    @PostMapping("order")
    @ResponseBody
    public ResultInfo payOrder(@RequestBody JSONObject params) {
        double totalFee = params.getDoubleValue(TOTALFEE_KEY);
        ResultInfo result = weixinPayService.payOrder(params);
        return result;
    }

}
