package com.love.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wxpay")
public class WXPayProperties {
	/**
	 * 微信网关地址
	 */
	private String apiUrl;
    
    private String appid;
    /**
     * 主商户号
     */
    private String mchId;
    
    private String mchKey;
    
	private String certAddress;
    
    /**
     * 循环次数
     */
    private int cycleNum;
    
    /**
     * 间隔毫秒时间
     */
    private int intervalTime;
    
    
    /**
     * 子商户号
     */
    private String subMchId;
    
    /**
     * 支付模式
     * 普通商户号 1
     * 服务商 2
     */
    private int payMode;


	public int getCycleNum() {
		return cycleNum;
	}

	public void setCycleNum(int cycleNum) {
		this.cycleNum = cycleNum;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

 

    /**
     * HTTP(S) 连接超时时间，单位毫秒
     *
     * @return
     */
    public int getHttpConnectTimeoutMs() {
        return 3*1000;
    }

    /**
     * HTTP(S) 读数据超时时间，单位毫秒
     *
     * @return
     */
    public int getHttpReadTimeoutMs() {
        return 4*1000;
    }
    
    public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getAppid() {
        return appid;
    }
    public void setAppid(String appid) {
        this.appid = appid;
    }
    public String getMchId() {
        return mchId;
    }
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }
    public String getMchKey() {
        return mchKey;
    }
    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

	public String getCertAddress() {
		return certAddress;
	}

	public void setCertAddress(String certAddress) {
		this.certAddress = certAddress;
	}

	public String getSubMchId() {
		return subMchId;
	}

	public void setSubMchId(String subMchId) {
		this.subMchId = subMchId;
	}

	public int getPayMode() {
		return payMode;
	}

	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}


    
    
    
    
}
