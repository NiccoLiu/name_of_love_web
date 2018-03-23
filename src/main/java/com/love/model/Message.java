package com.love.model;

public interface Message {
	
	/**
	 * 文本消息
	 */
	String MESSAGE_TEXT = "text";
	
	/**
	 * 关注消息
	 */
	String MESSAGE_SUBSCRIBE = "subscribe";
	
	/**
	 * 取消关注
	 */
	String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	
	/**
	 * 事件消息
	 */
	String MESSAGE_EVENT = "event";
	
	/**
	 * 点击拉取消息
	 */
	String MESSAGE_CLICK = "CLICK";
	
	/**
	 * 点击链接消息
	 */
	String MESSAGE_VIEW = "VIEW";
	
}
