package com.love;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.love.config.WechatProperties;
import com.love.model.MenuUtil;
import com.love.model.Token;
import com.love.service.RedisService;
import com.love.util.WechatHttpUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NameOfLoveWebApplicationTests {
    private final static Logger logger =
            LoggerFactory.getLogger(NameOfLoveWebApplicationTests.class);
    @Resource
    WechatProperties wechatProp;

    @Autowired
    RedisService redisService;

    @Test
    public void contextLoads() {}

    @Test
    public void generate() {

        String accessToken = redisService.get("token");
        if (StringUtils.isNotBlank(accessToken)) {
            Token token = WechatHttpUtil.getToken(wechatProp.getTokenUrl(), wechatProp.getAppid(),
                    wechatProp.getAppsecret());
            accessToken = token.getAccessToken();
            redisService.set("token", accessToken, 4800);
        }

        /**
         * 创建菜单 MENU_CREATE_URL 自定义菜单 PERSONAL_MENU_URL 个性化菜单
         */
        boolean result = MenuUtil.createMenu(wechatProp.getAppid(), wechatProp.getMenuCreateUrl(),
                accessToken);
        if (result) {
            logger.info("菜单创建成功");
        } else {
            logger.info("菜单创建失败");
        }

    }
}
