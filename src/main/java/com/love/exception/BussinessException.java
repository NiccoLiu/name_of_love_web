package com.love.exception;

/**
 * 业务异常的封装
 * 
 * @date 2016年11月12日 下午5:05:10
 */
@SuppressWarnings("serial")
public class BussinessException extends RuntimeException{

	//友好提示的code码
	private int friendlyCode;
	
	//友好提示
	private String friendlyMsg;
	
	//业务异常跳转的页面
	private String urlPath;
	
	public BussinessException(BizExceptionEnum bizExceptionEnum){
		this.friendlyCode = bizExceptionEnum.getCode();
		this.friendlyMsg = bizExceptionEnum.getMessage();
		this.urlPath = bizExceptionEnum.getUrlPath();
	}
	
	public BussinessException(int friendlyCode, String friendlyMsg){
		this.friendlyCode = friendlyCode;
		this.friendlyMsg = friendlyMsg;
	}

	public BussinessException(BusinessExceptionEnum businessExceptionEnum) {
		this.friendlyCode = businessExceptionEnum.getCode();
		this.friendlyMsg = businessExceptionEnum.getMsg();
	}

	public int getCode() {
		return friendlyCode;
	}

	public void setCode(int code) {
		this.friendlyCode = code;
	}

	public String getMessage() {
		return friendlyMsg;
	}

	public void setMessage(String message) {
		this.friendlyMsg = message;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	
}
