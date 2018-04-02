package com.love.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.love.config.WechatProperties;
import com.love.exception.BizExceptionEnum;
import com.love.exception.BussinessException;
import com.love.mapper.UserDAO;
import com.love.model.ResultInfo;
import com.love.model.User;
import com.love.service.ReceiverMessageService;
import com.love.service.RedisService;
import com.love.service.WechatConfigService;
import com.love.util.QRCodeUtil;
import com.love.util.SignUtil;
import com.love.util.WechatHttpUtil;

/**
 * 
 * @author Jekin
 * @date 2017年8月3日下午4:28:10
 */
@Controller
@RequestMapping("/config")
public class WechatConfigController {

    private static final Logger Logger = LoggerFactory.getLogger(WechatConfigController.class);

    public static final String AUTO_MODEL_MENU = "auto";

    public static final String ORDER_MODEL_MENU = "order";

    public static final String FEEDBAKC_MODEL_MENU = "feedback";


    @Resource
    WechatProperties wechatProp;

    @Resource
    WechatConfigService wechatConfigService;

    @Resource
    ReceiverMessageService receiverService;

    @Autowired
    private RedisService redisService;

    @Resource
    private UserDAO userDao;

    @RequestMapping(method = RequestMethod.GET, value = "/sign")
    public void signature(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String echostr = request.getParameter("echostr");

        PrintWriter out = response.getWriter();
        if (SignUtil.checkSignature(wechatProp.getToken(), request)) {
            out.print(echostr);
            String respXml = receiverService.receiveMessage(request);
            out.print(respXml);
        }
        out.close();
        out = null;
    }

    /**
     * 
     * @author Jekin
     * @date 2017年8月16日下午8:03:04
     * @description 图文消息回复
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/sign")
    public void sendMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        if (SignUtil.checkSignature(wechatProp.getToken(), request)) {
            // 接收处理请求
            String respXml = receiverService.receiveMessage(request);
            out.print(respXml);
        }
        out.close();
        out = null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/jssdk")
    @ResponseBody
    public ResultInfo configJsskd() {
        return wechatConfigService.configJssdk();
    }

    @GetMapping("/menu/{model}")
    public void queryDeviceInfo(HttpServletResponse response, @PathVariable("model") String model)
            throws IOException {
        String redirectUrl = setUrlEncodeUTF8(wechatProp.getAutoPayRedirectUrl());
        String url = wechatProp.getSnsapibaseUrl().replace("APPID", wechatProp.getAppid())
                .replace("REDIRECT_URI", redirectUrl).replace("STATE", model);
        response.sendRedirect(url);
    }

    private String setUrlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Logger.info("网页授权链接转换异常" + e.getMessage());
        }
        return result;
    }

    @RequestMapping("/redirectUrl")
    @ResponseBody
    public void redirectUrl(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String code = request.getParameter("code");
        String model = request.getParameter("state");
        Logger.info("code is {},model is {}", code, model);
        if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(model)) {
            String jsonString = WechatHttpUtil.requestUrl(
                    wechatProp.getOauthToken().replace("APPID", wechatProp.getAppid())
                            .replace("SECRET", wechatProp.getAppsecret()).replace("CODE", code),
                    "GET", null);
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String accessToken = jsonObject.getString("access_token");
            String openId = jsonObject.getString("openid");
            Logger.info("accessToken is:{}, openId is:{}", accessToken, openId);

            String userJson = WechatHttpUtil.requestUrl(wechatProp.getUserInfoUrl()
                    .replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId), "GET", null);
            JSONObject userObject = JSONObject.parseObject(userJson);
            Logger.info("get wechat user info is:{}", userObject);

            StringBuilder stringBuilder = new StringBuilder();
            User user = new User();
            user.setOpenid(openId);
            User userQuery = userDao.selectOne(user);
            if (userQuery == null) {
                user.setImageUrl(jsonObject.getString("headimgurl"));
                user.setName(jsonObject.getString("nickname"));
                userDao.insert(user);
            }
            if ("recommond".equals(model)) {
                stringBuilder.append("http://iot.1000mob.com/dev/config/menu/" + openId);
            } else {
                stringBuilder.append(wechatProp.getIndexHtml());
            }

            Logger.info("index url is {}", stringBuilder.toString());
            response.sendRedirect(stringBuilder.toString());
        } else {
            throw new BussinessException(BizExceptionEnum.ORDER_URL_ERROR);
        }
    }

    @PostMapping("/oauth/getUserInfo")
    @ResponseBody
    public ResultInfo getUserInfoByCode(@RequestBody JSONObject jsonObject) {
        return wechatConfigService.getUserInfoByCode(jsonObject);
    }

    @GetMapping("/generatorCode")
    public void generatorCode(HttpServletRequest request, HttpServletResponse response) {
        // String content = request.getQueryString();

        response.setContentType("image/jpeg");
        try {
            OutputStream out = response.getOutputStream();
            QRCodeUtil.encode("http://iot.1000mob.com/dev/config/menu/recommond", null, out, true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
