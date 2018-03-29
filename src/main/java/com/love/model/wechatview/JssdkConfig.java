package com.love.model.wechatview;

import java.util.List;

import com.love.config.BaseConfig;

public class JssdkConfig extends BaseConfig {

    /**
     * 
     */
    private static final long serialVersionUID = -8587801327611009173L;
    private Boolean debug;
    private String appId;
    private String signature;
    private List<String> jsApiList;

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<String> getJsApiList() {
        return jsApiList;
    }

    public void setJsApiList(List<String> jsApiList) {
        this.jsApiList = jsApiList;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


}
