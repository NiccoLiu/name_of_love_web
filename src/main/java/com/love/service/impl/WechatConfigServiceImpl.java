package com.love.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.love.config.SmallRoutineProperties;
import com.love.config.WechatProperties;
import com.love.mapper.UserDAO;
import com.love.model.ResultInfo;
import com.love.model.Token;
import com.love.model.User;
import com.love.model.wechatview.JssdkConfig;
import com.love.service.RedisService;
import com.love.service.UserService;
import com.love.service.WechatConfigService;
import com.love.util.CommonUtil;
import com.love.util.SHA1Util;
import com.love.util.WechatHttpUtil;

@Service
@Transactional
public class WechatConfigServiceImpl implements WechatConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatConfigServiceImpl.class);
    private static final String CAPCHA_NUMBER = "0123456789";
    @Resource
    WechatProperties wechatProp;
    @Resource
    private SmallRoutineProperties smallRoutineProperties;

    @Autowired
    RedisService redisService;
    @Autowired
    private UserService userServiceImpl;
    @Resource
    private UserDAO userDao;

    @Override
    public ResultInfo getUserInfoByCode(String code, String model) {
        ResultInfo resultInfo = new ResultInfo(0, "success");

        String dataString = WechatHttpUtil.requestUrl(
                wechatProp.getOauthToken().replace("APPID", wechatProp.getAppid())
                        .replace("SECRET", wechatProp.getAppsecret()).replace("CODE", code),
                "GET", null);
        LOGGER.info("get wechat openId info is:{}", dataString);
        JSONObject dataObject = JSONObject.parseObject(dataString);
        String access_token = dataObject.getString("access_token");
        String openId = dataObject.getString("openid");

        String userJson = WechatHttpUtil.requestUrl(wechatProp.getUserInfoUrl()
                .replace("ACCESS_TOKEN", access_token).replace("OPENID", openId), "GET", null);
        JSONObject userObject = JSONObject.parseObject(userJson);
        LOGGER.info("get wechat user info is:{}", userObject);
        User paramEntity = new User();
        paramEntity.setOpenid(openId);
        User user = userServiceImpl.query(paramEntity);
        if (user == null) {
            user.setImageUrl(userObject.getString("headimgurl"));
            user.setName(userObject.getString("nickname"));
            if (!"index".equals(model)) {
                user.setSource(redisService.get(model));
            }
            userDao.insert(user);
        }
        resultInfo.setData(user);
        return resultInfo;
    }

    @Override
    public ResultInfo configJssdk(JSONObject params) {
        String sessionKey = params.getString("sessionKey");
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
            sortedMap.put("url", wechatProp.getWebIndex() + "?sessionKey=" + sessionKey);
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

    @Override
    public ResultInfo getSessionKey(String code) {
        ResultInfo resultInfo = new ResultInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        String WX_URL =
                "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
        String WX_UNION_URL =
                "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        try {
            if (StringUtils.isBlank(code)) {
                resultInfo.setMsg("Code为空!");
            } else {
                String requestUrl = WX_URL.replace("APPID", smallRoutineProperties.getAppid())
                        .replace("SECRET", smallRoutineProperties.getAppsecret())
                        .replace("JSCODE", code)
                        .replace("authorization_code", "authorization_code");
                LOGGER.info(requestUrl);
                // 发起GET请求获取凭证
                JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
                LOGGER.info("wechat getSessionKey response is {}", jsonObject);
                if (jsonObject != null) {
                    try {
                        String openId = jsonObject.getString("openid");
                        String sessionKey = jsonObject.getString("session_key");
                        String unionId = jsonObject.getString("unionid");
                        map.put("openid", openId);
                        map.put("sessionkey", sessionKey);
                        map.put("unionId", unionId);
                        // 生成6位随机数
                        StringBuffer rands = new StringBuffer();
                        for (int i = 0; i < 6; i++) {
                            int rand = (int) (Math.random() * 10);
                            char str = CAPCHA_NUMBER.charAt(rand);
                            rands.append(str + "");
                        }
                        String rdSession = rands.append(openId).toString();

                        redisService.set(rdSession, JSONObject.toJSONString(map));
                        LOGGER.info("rdSession in redis is {},the key is {}",
                                redisService.get(rdSession), rdSession);
                        Map<String, Object> mapSession = new HashMap<>(2);
                        mapSession.put("rdSession", rdSession);

                        resultInfo.setData(mapSession);
                    } catch (JSONException e) {
                        resultInfo.setMsg("code无效!");
                    }
                } else {
                    resultInfo.setMsg("code无效!");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return resultInfo;
    }
}
