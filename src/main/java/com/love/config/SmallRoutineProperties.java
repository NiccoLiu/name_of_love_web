package com.love.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Jekin
 * @date 2017年8月31日下午4:57:29
 * @description 微信配置文件
 */
@Component
@ConfigurationProperties(prefix = SmallRoutineProperties.PREFIX)
public class SmallRoutineProperties {

    public static final String PREFIX = "smallroutine";

    private String appid;

    private String appsecret;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

}
