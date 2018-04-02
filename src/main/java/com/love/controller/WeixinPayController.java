package com.love.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.love.model.ResultInfo;
import com.love.service.WeixinPayService;
import com.love.util.WXPayUtil;

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

    /**
     * 微信支付通知处理
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @PostMapping("payback")
    public void payback(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        String result = new String(outSteam.toByteArray(), "utf-8");

        if (StringUtils.isEmpty(result)) {
            writer.write("request parameter is empty!");
            return;
        }

        Map<String, String> map = null;
        try {
            map = WXPayUtil.xmlToMap(result);
        } catch (Exception e) {
            logger.error("wxpay order notify error ,the response is {}", result, e);
        }
        try {
            String backToWeixinXml = weixinPayService.payBack(map);
            logger.info("weixin payback notify xml is {} ,return to weixin xml is {}", result,
                    backToWeixinXml);
            writer.write(backToWeixinXml);
            writer.flush();
        } catch (Exception e) {
            logger.error("wxpay order notify to xml string error ,the response is {}", result, e);
        }
    }
}
