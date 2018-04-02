package com.love.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.love.model.ResultInfo;

/**
 * 微信支付请求
 *
 * @author verne
 */
public interface WeixinPayService {
    /**
     * 统一下单，参考微信API:https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
     * 
     * @param params 请求参数
     * @return APP支付SDK需要数据
     */
    ResultInfo payOrder(JSONObject params);

    String payBack(Map<String, String> map) throws Exception;


}
