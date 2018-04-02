package com.love.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.love.config.WechatProperties;
import com.love.model.ResultInfo;
import com.love.service.WeixinPayService;

/**
 * 微信支付接口
 *
 * @author liuxinq
 */
@Service
public class WerxinPayServiceImpl implements WeixinPayService {
    private static final Logger logger = LoggerFactory.getLogger(WerxinPayServiceImpl.class);
    private static final String TRADE_NO_PREFIX = "wxno";
    private static final String TOTALFEE_KEY = "totalAmount";
    private static final int EXPIRE_TIME_MINUTES = 10;
    @Resource
    WechatProperties wechatProp;
    @Resource
    WXPayImpl wxPayImpl;

    @Override
    public ResultInfo payOrder(JSONObject params) {
        ResultInfo result = new ResultInfo(0, "success");
        try {
            String wxTradeNo = TRADE_NO_PREFIX + IdWorker.getId();
            double totalFee = params.getDoubleValue(TOTALFEE_KEY);
            String openId = params.getString("openid");
            Date nowTime = new Date();
            String expireTime = DateFormatUtils
                    .format(DateUtils.addMinutes(nowTime, EXPIRE_TIME_MINUTES), "yyyMMddHHmmss");
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("body", "以爱为名健康出行-会员充值");
            data.put("openid", openId);
            data.put("out_trade_no", wxTradeNo);// 商户订单号
            data.put("spbill_create_ip", "47.96.141.185");
            data.put("notify_url", "http://iot.1000mob.com/dev/config/jssdk");
            data.put("trade_type", "JSAPI");
            data.put("fee_type", "CNY");
            // FIXME 测试阶段写死金额
            data.put("total_fee", "1");// weixinPayModel.setTotalFee(1);
            data.put("time_expire", expireTime);
            Map<String, String> responseData = null;
            responseData = wxPayImpl.unifiedOrder(data);
            logger.info("result is {}", responseData);
            result.setData(responseData);
        } catch (Exception e) {
            logger.error("wxpay order error ,the params is {}", params, e);
        }
        return result;
    }

}
