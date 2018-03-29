package com.love.model;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.love.model.wechatview.Button;
import com.love.model.wechatview.Menu;
import com.love.model.wechatview.ViewButton;
import com.love.util.WechatHttpUtil;

public class MenuUtil {

    private static Logger Logger = LoggerFactory.getLogger(MenuUtil.class);

    public static boolean createMenu(String appid, String menuUrl, String accessToken) {
        boolean result = false;
        String url = menuUrl.replace("ACCESS_TOKEN", accessToken);
        String jsonMenu = JSONObject.toJSONString(getMenu(appid));
        Logger.info("menu json string:{}", jsonMenu);
        JSONObject jsonObject = WechatHttpUtil.httpsRequest(url, "POST", jsonMenu);

        if (jsonObject != null) {
            int errorCode = jsonObject.getIntValue("errcode");
            String errorMsg = jsonObject.getString("errmsg");
            if (errorCode == 0) {
                result = true;
            } else {
                result = false;
                Logger.error("创建菜单失败errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return result;
    }

    public static Menu getMenu(String appid) {

        ViewButton btn1 = new ViewButton();
        btn1.setName("首页");
        btn1.setType("view");
        btn1.setUrl("https://www.baidu.com");

        ViewButton btn2 = new ViewButton();
        btn2.setName("推广");
        btn2.setType("view");
        btn2.setUrl("https://www.baidu.com");

        ViewButton btn3 = new ViewButton();
        btn3.setName("我的");
        btn3.setType("view");
        btn3.setUrl("https://www.baidu.com");

        Menu menu = new Menu();
        menu.setButton(new Button[] {btn1, btn2, btn3});

        return menu;

    }

    public static String setUrlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
