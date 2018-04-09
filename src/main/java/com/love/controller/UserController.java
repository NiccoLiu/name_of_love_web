package com.love.controller;


import java.io.File;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.love.config.WechatProperties;
import com.love.mapper.SignDAO;
import com.love.model.ResultInfo;
import com.love.model.Sign;
import com.love.model.Token;
import com.love.model.User;
import com.love.service.RedisService;
import com.love.service.UserService;
import com.love.util.WechatHttpUtil;

/**
 * : Controller类
 * 
 * @author generator
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {


    @Autowired
    private UserService userServiceImpl;
    @Resource
    private RedisService redisService;
    @Resource
    private WechatProperties wechatProp;
    @Resource
    private SignDAO signDAO;

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
        return userServiceImpl.add(params);
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
        User paramEntity = params.toJavaObject(User.class);
        String sessionKey = params.getString("sessionKey");
        String openId = redisService.get(sessionKey);
        paramEntity.setOpenid(openId);
        User user = userServiceImpl.query(paramEntity);

        String accessToken = redisService.get("token");
        if (accessToken == null) {
            Token token = WechatHttpUtil.getToken(wechatProp.getTokenUrl(), wechatProp.getAppid(),
                    wechatProp.getAppsecret());
            accessToken = token.getAccessToken();
            redisService.set("token", accessToken, 4800);
        }
        String userJson = WechatHttpUtil.requestUrl(wechatProp.getUserInfoUrl1()
                .replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId), "GET", null);
        JSONObject userObject = JSONObject.parseObject(userJson);
        user.setSubscribe(userObject.getInteger("subscribe"));
        JSONObject responseJson = (JSONObject) JSONObject.toJSON(user);
        // 是否签到 0 没有 1是
        Sign sign = new Sign();
        sign.setOpenid(openId);
        sign.setCreateTime(new Date());
        Sign result = signDAO.findSign(sign);
        if (result != null) {
            responseJson.put("isSign", 1);
        } else {
            responseJson.put("isSign", 0);
        }

        responseJson.put("url",
                wechatProp.getTemplateUrl() + File.separator + "config/menu/" + sessionKey);
        responseJson.put("message", "今天我又领了10元现金");
        resultInfo.setData(responseJson);
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
        User user = params.toJavaObject(User.class);
        ResultInfo resultInfo = userServiceImpl.update(user);
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
        ResultInfo resultInfo = userServiceImpl.delete(params);
        return resultInfo;
    }

    /**
     * 分享信息
     * 
     * 
     * @param params jsonstring
     * @return code and msg
     */
    @PostMapping("shareInfo")
    @ResponseBody
    public ResultInfo shareInfo(@RequestBody JSONObject params) {
        ResultInfo resultInfo = userServiceImpl.shareInfo(params);
        return resultInfo;
    }


}
