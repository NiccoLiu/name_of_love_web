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
@ConfigurationProperties(prefix = WechatProperties.PREFIX)
public class WechatProperties {

    public static final String PREFIX = "wechat";

    private String appid;

    private String appsecret;

    private String token;

    private String tokenUrl;

    private String oauthToken;

    private String refreshToken;

    private String appUserInfoUrl;

    private String userInfoUrl;

    private String snsapibaseUrl;

    private String menuCreateUrl;

    private String personalMenuUrl;

    private String jsapiTicket;

    private String webIndex;

    private String autoPayRedirectUrl;

    private String templateUrl;

    private String indexHtml;

    private String carManagerHtml;

    private String orderHtml;

    private String feedbackHtml;

    private String agreementHtml;

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getAppUserInfoUrl() {
        return appUserInfoUrl;
    }

    public void setAppUserInfoUrl(String appUserInfoUrl) {
        this.appUserInfoUrl = appUserInfoUrl;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }

    public String getSnsapibaseUrl() {
        return snsapibaseUrl;
    }

    public void setSnsapibaseUrl(String snsapibaseUrl) {
        this.snsapibaseUrl = snsapibaseUrl;
    }

    public String getMenuCreateUrl() {
        return menuCreateUrl;
    }

    public void setMenuCreateUrl(String menuCreateUrl) {
        this.menuCreateUrl = menuCreateUrl;
    }

    public String getPersonalMenuUrl() {
        return personalMenuUrl;
    }

    public void setPersonalMenuUrl(String personalMenuUrl) {
        this.personalMenuUrl = personalMenuUrl;
    }

    public String getJsapiTicket() {
        return jsapiTicket;
    }

    public String getWebIndex() {
        return webIndex;
    }

    public void setWebIndex(String webIndex) {
        this.webIndex = webIndex;
    }

    public void setJsapiTicket(String jsapiTicket) {
        this.jsapiTicket = jsapiTicket;
    }

    public String getAutoPayRedirectUrl() {
        return autoPayRedirectUrl;
    }

    public void setAutoPayRedirectUrl(String autoPayRedirectUrl) {
        this.autoPayRedirectUrl = autoPayRedirectUrl;
    }

    public String getIndexHtml() {
        return indexHtml;
    }

    public void setIndexHtml(String indexHtml) {
        this.indexHtml = indexHtml;
    }

    public String getCarManagerHtml() {
        return carManagerHtml;
    }

    public void setCarManagerHtml(String carManagerHtml) {
        this.carManagerHtml = carManagerHtml;
    }

    public String getOrderHtml() {
        return orderHtml;
    }

    public void setOrderHtml(String orderHtml) {
        this.orderHtml = orderHtml;
    }

    public String getFeedbackHtml() {
        return feedbackHtml;
    }

    public void setFeedbackHtml(String feedbackHtml) {
        this.feedbackHtml = feedbackHtml;
    }

    public String getAgreementHtml() {
        return agreementHtml;
    }

    public void setAgreementHtml(String agreementHtml) {
        this.agreementHtml = agreementHtml;
    }



}
