package com.love.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.love.config.WXPayProperties;
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
    private static final String TRADE_NO_PREFIX = "love";
    private static final String TOTALFEE_KEY = "totalAmount";
    private static final long APP_DATA_CACHED_ALIVE_TIME = 60 * 12;
    private static final String OUT_TRADE_NO_KEY = "out_trade_no";
    private static final String WX_RESULT_SUCCESS = "SUCCESS";
    private static final String PREPAY_ID = "prepay_id=";
    @Resource
    WechatProperties wechatProp;
    @Resource
    WXPayProperties wxPayProperties;
    @Resource
    WXPayImpl wxPayImpl;
    @Resource
    RedisService redisService;
    @Resource
    private OrderDAO orderService;
    @Resource
    private UserDAO userService;
    @Resource
    private OrderDAO orderDAO;

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
            String phone = params.getString("phone");
            String openId = redisService.get(sessionKey);
            if (openId == null) {
                result.setCode(-1);
                result.setMsg("sessionKey不存在！");
                return result;
            }
            /*
             * Date nowTime = new Date();
             * 
             * String expireTime = DateFormatUtils .format(DateUtils.addMinutes(nowTime,
             * EXPIRE_TIME_MINUTES), "yyyMMddHHmmss");
             */
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("body", "以爱为名 健康出行-会员充值");
            data.put("openid", openId);
            data.put("out_trade_no", wxTradeNo);// 商户订单号
            data.put("spbill_create_ip", HttpKit.getRequest().getRemoteAddr());
            data.put("notify_url", wechatProp.getTemplateUrl() + File.separator + "wxpay/payback");
            data.put("trade_type", "JSAPI");
            // data.put("fee_type", "CNY");
            data.put("total_fee", "1");// weixinPayModel.setTotalFee(1);
            // data.put("time_expire", expireTime);
            Map<String, String> responseData = null;
            redisService.set(wxTradeNo, data.toString(), APP_DATA_CACHED_ALIVE_TIME);
            responseData = wxPayImpl.unifiedOrder(data);
            SortedMap<String, String> parameters = new TreeMap<String, String>();

            String timestamp = "" + System.currentTimeMillis() / 1000;
            parameters.put("appId", responseData.get("appid"));
            parameters.put("timeStamp", timestamp);
            parameters.put("nonceStr", responseData.get("nonce_str"));
            parameters.put("package", PREPAY_ID + responseData.get("prepay_id"));
            parameters.put("signType", WXPayConstants.HMACSHA256);
            String sign = paySign(parameters);
            parameters.put("sign", sign);

            if ("SUCCESS".equals(responseData.get("return_code"))) {
                if (phone != null && !"".equals(phone)) {
                    User user = new User();
                    user.setPhone(phone);
                    User userNew = userService.selectOne(user);
                    OrderDetail order = new OrderDetail();
                    if (userNew != null) {
                        order.setOpenid(userNew.getOpenid());
                    }
                    order.setPhone(phone);
                    order.setAmount(new BigDecimal(totalFee));
                    order.setPayType(1);
                    order.setSerialNumber(wxTradeNo);
                    order.setPayer(openId);
                    orderService.insert(order);
                } else {
                    OrderDetail order = new OrderDetail();
                    order.setOpenid(openId);
                    order.setAmount(new BigDecimal(totalFee));
                    order.setPayType(1);
                    order.setSerialNumber(wxTradeNo);
                    orderService.insert(order);
                }

            }
            logger.info("result is {}", responseData);
            result.setData(parameters);
        } catch (Exception e) {
            logger.error("wxpay order error ,the params is {}", params, e);
        }
        return result;
    }

    private String paySign(SortedMap<String, String> parameters) throws Exception {
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> es = parameters.entrySet();
        Iterator<Entry<String, String>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k);
                sb.append("=");
                sb.append(v);
                sb.append("&");
            }
        }

        sb.append("key=" + wxPayProperties.getMchKey());

        String sign = WXPayUtil.HMACSHA256(sb.toString(), wxPayProperties.getMchKey());

        logger.debug("parameters  is {} ,sign is {}", sb.toString(), sign);
        return sign;
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
        // String totalFee = responseData.get("total_fee");
        // BigDecimal balance = new BigDecimal(totalFee).divide(new BigDecimal("100"));
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
                orderDetail.setWechatNumber(responseData.get("transaction_id"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String endTime = responseData.get("time_end");
                Date date = sdf.parse(endTime);
                orderDetail.setEndTime(date);
                // 修改订单表
                orderService.update(orderDetail,
                        new EntityWrapper<OrderDetail>().eq("serial_number", tradeNo));

                OrderDetail orderDetailNew = new OrderDetail();
                orderDetailNew.setSerialNumber(tradeNo);
                orderDetailNew = orderService.selectOne(orderDetailNew);
                // 修改用户余额
                User user = new User();
                user.setOpenid(orderDetailNew.getOpenid());
                user = userService.selectOne(user);
                if (user.getOldMember() != 1 && user.getSource() != null
                        && !"".equals(user.getSource())) {
                    String resource = user.getSource();
                    User resourceUser = new User();
                    resourceUser.setOpenid(resource);
                    resourceUser = userService.selectOne(resourceUser);
                    if (resourceUser != null) {
                        Map<String, Object> columnMap = new HashMap<>(3);
                        columnMap.put("openid", resource);
                        columnMap.put("pay_type", 1);
                        columnMap.put("pay_result", 1);
                        List<OrderDetail> lists = orderDAO.selectByMap(columnMap);
                        BigDecimal twoDecimal = new BigDecimal("2");
                        for (Iterator<OrderDetail> iterator = lists.iterator(); iterator
                                .hasNext();) {
                            OrderDetail orderDetail2 = (OrderDetail) iterator.next();
                            BigDecimal cashBack = orderDetail2.getCashback().add(twoDecimal);
                            int days = daysBetween(orderDetail2.getCreateTime(), new Date());
                            if (days <= 180 && orderDetail2.getAmount().doubleValue() >= cashBack
                                    .doubleValue()) {
                                resourceUser.setBalance(resourceUser.getBalance().add(twoDecimal));
                                resourceUser
                                        .setCashToday(resourceUser.getCashToday().add(twoDecimal));
                                resourceUser
                                        .setCashShare(resourceUser.getCashShare().add(twoDecimal));
                                resourceUser
                                        .setCashBack(resourceUser.getCashBack().add(twoDecimal));
                                userService.update(resourceUser,
                                        new EntityWrapper<User>().eq("openid", resource));
                                orderDetail2.setCashback(cashBack);
                                orderDAO.updateById(orderDetail2);
                                break;
                            }
                        }

                    }

                }
                // user.setBalance(user.getBalance().add(balance));
                user.setOldMember(1);
                userService.update(user, new EntityWrapper<User>().eq("openid", user.getOpenid()));

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


    @Override
    public ResultInfo withdrawals(JSONObject params) {
        ResultInfo result = new ResultInfo(0, "success");
        try {
            String wxTradeNo = TRADE_NO_PREFIX + IdWorker.getId();
            double totalFee = params.getDoubleValue(TOTALFEE_KEY);

            String sessionKey = params.getString("sessionKey");
            String openId = redisService.get(sessionKey);
            User user = new User();
            user.setOpenid(openId);
            user = userService.selectOne(user);
            if (user.getBalance().doubleValue() < totalFee) {
                result.setCode(-1);
                result.setData("您的余额不足!");
                return result;
            }
            if (openId == null) {
                result.setCode(-1);
                result.setMsg("sessionKey不存在！");
                return result;
            }
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("check_name", "NO_CHECK");
            data.put("openid", openId);
            // 商户订单号
            data.put("partner_trade_no", wxTradeNo);
            BigDecimal amount = new BigDecimal(totalFee * 100);
            data.put("amount", amount.toBigInteger() + "");
            data.put("desc", "以爱为名 健康出行奖励提现");
            data.put("spbill_create_ip", HttpKit.getRequest().getRemoteAddr());
            Map<String, String> responseData = null;

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOpenid(openId);
            orderDetail.setPayType(2);
            orderDetail.setPayResult(0);
            orderDetail.setAmount(new BigDecimal(totalFee));
            // orderDetail.setEndTime(new Date());
            orderDetail.setSerialNumber(wxTradeNo);
            orderService.insert(orderDetail);
            // orderDetail.setWechatNumber(responseData.get("payment_no"));
            user.setBalance(user.getBalance().subtract(new BigDecimal(totalFee)));
            userService.update(user, new EntityWrapper<User>().eq("openid", openId));
            responseData = wxPayImpl.withdrawalsOrder(data);

            logger.info("result is {}", responseData);
            // result.setData(result);
        } catch (Exception e) {
            logger.error("wxpay withdrawals error ,the params is {}", params, e);
        }
        return result;
    }

    public static final int daysBetween(Date early, Date late) {

        java.util.Calendar calst = java.util.Calendar.getInstance();
        java.util.Calendar caled = java.util.Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        // 设置时间为0时
        calst.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calst.set(java.util.Calendar.MINUTE, 0);
        calst.set(java.util.Calendar.SECOND, 0);
        caled.set(java.util.Calendar.HOUR_OF_DAY, 0);
        caled.set(java.util.Calendar.MINUTE, 0);
        caled.set(java.util.Calendar.SECOND, 0);
        // 得到两个日期相差的天数
        int days = ((int) (caled.getTime().getTime() / 1000)
                - (int) (calst.getTime().getTime() / 1000)) / 3600 / 24;

        return days;
    }
}
