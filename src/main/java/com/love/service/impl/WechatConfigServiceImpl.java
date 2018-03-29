package com.love.service.impl;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.love.config.WechatProperties;
import com.love.model.ResultInfo;
import com.love.model.Token;
import com.love.model.wechatview.JssdkConfig;
import com.love.service.RedisService;
import com.love.service.WechatConfigService;
import com.love.util.SHA1Util;
import com.love.util.WechatHttpUtil;

@Service
@Transactional
public class WechatConfigServiceImpl implements WechatConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatConfigServiceImpl.class);

    @Resource
    WechatProperties wechatProp;

    @Autowired
    RedisService redisService;

    @Override
    public ResultInfo getUserInfoByCode(JSONObject jsonObject) {
        ResultInfo resultInfo = new ResultInfo(0, "success");
        String code = jsonObject.getString("code");

        String dataString = WechatHttpUtil.requestUrl(
                wechatProp.getOauthToken().replace("APPID", wechatProp.getAppid())
                        .replace("SECRET", wechatProp.getAppsecret()).replace("CODE", code),
                "GET", null);
        LOGGER.info("get wechat openId info is:{}", dataString);
        JSONObject dataObject = JSONObject.parseObject(dataString);
        String access_token = dataObject.getString("access_token");
        String openId = dataObject.getString("openid");

        String userJson = WechatHttpUtil.requestUrl(wechatProp.getAppUserInfoUrl()
                .replace("ACCESS_TOKEN", access_token).replace("OPENID", openId), "GET", null);
        JSONObject userObject = JSONObject.parseObject(userJson);
        LOGGER.info("get wechat user info is:{}", userObject);
        resultInfo.setData(userJson);
        return resultInfo;
    }

    @Override
    public ResultInfo configJssdk() {
        ResultInfo resultInfo = new ResultInfo(0, "success");

        boolean isExit = redisService.exists("token");
        String token = null;
        if (isExit == true) {
            token = redisService.get("token");
        } else {
            token = saveToken(token);
        }

        if (token != null) {
            boolean isExitTicket = redisService.exists("jsapi_ticket");
            String ticket = null;
            if (isExitTicket == true) {
                ticket = redisService.get("jsapi_ticket");
            } else {
                JSONObject jsonObject = null;
                try {
                    jsonObject = WechatHttpUtil.httpsRequest(
                            wechatProp.getJsapiTicket().replace("ACCESS_TOKEN", token), "GET",
                            null);
                } catch (Exception e) {
                    LOGGER.info("token获取jsapi_ticket异常：" + e);
                    jsonObject = WechatHttpUtil.httpsRequest(
                            wechatProp.getJsapiTicket().replace("ACCESS_TOKEN", saveToken(token)),
                            "GET", null);
                } finally {
                    ticket = jsonObject.getString("ticket");
                    if (ticket != null) {
                        redisService.set("jsapi_ticket", ticket, 4800);
                    }
                }
            }

            JssdkConfig jsConfig = new JssdkConfig();
            jsConfig.setTimestamp(WechatHttpUtil.getUnixTime(new Date()));
            jsConfig.setNonceStr(WechatHttpUtil.getStringRandom());

            // 签名
            SortedMap<Object, Object> sortedMap = new TreeMap<>();
            sortedMap.put("noncestr", jsConfig.getNonceStr());
            sortedMap.put("jsapi_ticket", ticket);
            sortedMap.put("timestamp", jsConfig.getTimestamp());
            sortedMap.put("url", wechatProp.getWebIndex());
            LOGGER.info("get sign map:{}", sortedMap);
            String sign = SHA1Util.sha1Sign(sortedMap);
            if (sign == null) {
                resultInfo.setMsg("签名失败");
                return resultInfo;
            }
            jsConfig.setSignature(sign);
            jsConfig.setAppId(wechatProp.getAppid());
            resultInfo.setData(jsConfig);
        } else {
            resultInfo.setMsg("请求失败");
        }

        return resultInfo;
    }

    private String saveToken(String token) {
        Token accessToken = WechatHttpUtil.getToken(wechatProp.getTokenUrl(), wechatProp.getAppid(),
                wechatProp.getAppsecret());
        token = accessToken.getAccessToken();
        if (token != null) {
            redisService.set("token", token, 4800);
        }
        return token;
    }


}
