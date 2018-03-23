package com.love.config;

import java.io.Serializable;

public class BaseConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -339175209887264919L;
	
	private Long timestamp;
	private String nonceStr;
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	
	
	
}
