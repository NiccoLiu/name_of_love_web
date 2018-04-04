package com.love.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.love.config.WechatProperties;
import com.love.mapper.OrderDAO;
import com.love.mapper.UserDAO;
import com.love.model.OrderDetail;
import com.love.model.ResultInfo;
import com.love.model.User;
import com.love.service.RedisService;
import com.love.service.WeixinPayService;
import com.love.util.HttpKit;
import com.love.util.WXPayConstants;
import com.love.util.WXPayUtil;

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
    private static final long APP_DATA_CACHED_ALIVE_TIME = 60 * 12;
    private static final String TRANSACTION_ID_KEY = "transaction_id";
    private static final String THIRD_PAYMENT_NO_KEY = "third_trade_no";
    private static final String OUT_TRADE_NO_KEY = "out_trade_no";
    private static final String WX_RESULT_SUCCESS = "SUCCESS";
    private static final String ORDER_TOTAL_FEE_KEY = "total_fee";
    private static final String ORDER_TOTAL_AMOUNT_KEY = "total_amount";
    private static final String APP_KEY_PREFIX = "app.pay";
    private static final String BANK_TYPE_KEY = "bank_type";
    private static final String TIME_END_KEY = "time_end";
    private static final String TRADENO_KEY = "outrade_no";
    private static final String PAY_TYPE_KEY = "pay_type";
    private static final int ALI_PAY_TYPE_VALUE = 1;
    private static final String PAYTIME_KEY = "pay_time";
    @Resource
    WechatProperties wechatProp;
    @Resource
    WXPayImpl wxPayImpl;
    @Resource
    RedisService redisService;
    @Resource
    private OrderDAO orderService;
    @Resource
    private UserDAO userService;

    private static class notifyInfo {
        private static final String BACK_TO_WEIXIN_SUCCESSED_CODE = "SUCCESS";
        private static final String BACK_TO_WEIXIN_FAILED_CODE = "ERROR";
        private static final String BACK_TO_WEIXIN_SUCCESSED_MSG = "OK";
        private static final String BACK_TO_WEIXIN_FAILED_MSG = "FAILED";
    }

    @Override
    public ResultInfo payOrder(JSONObject params) {
        ResultInfo result = new ResultInfo(0, "success");
        try {
            String wxTradeNo = TRADE_NO_PREFIX + IdWorker.getId();
            double totalFee = params.getDoubleValue(TOTALFEE_KEY);
            String sessionKey = params.getString("sessionKey");
            String openId = redisService.get(sessionKey);
            if (openId == null) {
                result.setCode(-1);
                result.setMsg("sessionKey不存在！");
                return result;
            }
            Date nowTime = new Date();
            String expireTime = DateFormatUtils
                    .format(DateUtils.addMinutes(nowTime, EXPIRE_TIME_MINUTES), "yyyMMddHHmmss");
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("body", "以爱为名 健康出行-会员充值");
            data.put("openid", openId);
            data.put("out_trade_no", wxTradeNo);// 商户订单号
            data.put("spbill_create_ip", HttpKit.getRequest().getRemoteAddr());
            data.put("notify_url", wechatProp.getTemplateUrl() + File.separator + "wxpay/payback");
            data.put("trade_type", "JSAPI");
            // data.put("fee_type", "CNY");
            // FIXME 测试阶段写死金额
            data.put("total_fee", "1");// weixinPayModel.setTotalFee(1);
            // data.put("time_expire", expireTime);
            Map<String, String> responseData = null;
            redisService.set(wxTradeNo, data.toString(), APP_DATA_CACHED_ALIVE_TIME);
            responseData = wxPayImpl.unifiedOrder(data);
            if ("SUCCESS".equals(responseData.get("return_code"))) {
                OrderDetail order = new OrderDetail();
                order.setOpenid(openId);
                order.setAmount(new BigDecimal(totalFee));
                order.setPayType(1);
                order.setSerialNumber(wxTradeNo);
                orderService.insert(order);
            }
            logger.info("result is {}", responseData);
            result.setData(responseData);
        } catch (Exception e) {
            logger.error("wxpay order error ,the params is {}", params, e);
        }
        return result;
    }

    @Override
    public String payBack(Map<String, String> responseData) throws Exception {
        logger.debug("wxpay order notify data {}", responseData);
        Map<String, String> data = new HashMap<String, String>();
        data.put(WXPayConstants.WX_RETURN_CODE_KEY, notifyInfo.BACK_TO_WEIXIN_SUCCESSED_CODE);
        data.put(WXPayConstants.WX_RESULT_MSG_KEY, notifyInfo.BACK_TO_WEIXIN_SUCCESSED_MSG);
        OrderDetail orderDetail = new OrderDetail();
        String redisKey = responseData.get("out_trade_no");
        String openId = responseData.get("openid");
        // 单位是分
        String totalFee = responseData.get("total_fee");
        BigDecimal balance = new BigDecimal(totalFee).divide(new BigDecimal("100"));
        if (WX_RESULT_SUCCESS.equals(responseData.get(WXPayConstants.WX_RETURN_CODE_KEY))) {


            String cachedStr = redisService.get(redisKey);
            if (StringUtils.isEmpty(cachedStr)) {

                logger.warn("wxpay order payback many times,notify data is {}", responseData);

                return WXPayUtil.mapToXml(data);
            }
            String tradeNo = responseData.get(OUT_TRADE_NO_KEY);

            String xml;
            try {
                orderDetail.setPayResult(1);
                orderDetail.setBankType(responseData.get("bank_type"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endTime = responseData.get("time_end");
                Date date = sdf.parse(endTime);
                orderDetail.setEndTime(date);
                // 修改订单表
                orderService.update(orderDetail,
                        new EntityWrapper<OrderDetail>().eq("serial_number", tradeNo));

                // 修改用户余额
                User user = new User();
                user.setOpenid(openId);
                user = userService.selectOne(user);
                user.setBalance(user.getBalance().add(balance));
                userService.update(user, new EntityWrapper<User>().eq("openid", openId));
                xml = WXPayUtil.mapToXml(data);
                logger.debug("wxpay order payback response xml is {}", xml);
                // 删除缓存
                redisService.del(redisKey);
                return xml;
            } catch (Exception e) {
                logger.error("map to xml error", e);
                data.put(WXPayConstants.WX_RETURN_CODE_KEY, notifyInfo.BACK_TO_WEIXIN_FAILED_CODE);
                data.put(WXPayConstants.WX_RESULT_MSG_KEY, notifyInfo.BACK_TO_WEIXIN_FAILED_MSG);

            }
            // 获取缓存数据

        }
        orderDetail.setPayResult(2);
        orderService.update(orderDetail,
                new EntityWrapper<OrderDetail>().eq("serial_number", redisKey));
        data.put(WXPayConstants.WX_RETURN_CODE_KEY, notifyInfo.BACK_TO_WEIXIN_FAILED_CODE);
        data.put(WXPayConstants.WX_RESULT_MSG_KEY, notifyInfo.BACK_TO_WEIXIN_FAILED_MSG);
        String backToWeixinXml = WXPayUtil.mapToXml(data);
        logger.debug("wxpay order notify error, response xml is {}", backToWeixinXml);
        return backToWeixinXml;
    }

    private void cacheOrderData(String wxTradeNo, double totalFee, Long userId) {
        JSONObject cachedData = new JSONObject();
        cachedData.put(ORDER_TOTAL_AMOUNT_KEY, totalFee);
        cachedData.put(TRADENO_KEY, wxTradeNo);
        Date payTime = new Date();
        cachedData.put(PAYTIME_KEY, payTime);

        String redisKey = APP_KEY_PREFIX + wxTradeNo;
        redisService.set(redisKey, cachedData.toJSONString(), APP_DATA_CACHED_ALIVE_TIME);
    }
}
