package com.love.exception;

/**
 * 所有业务异常的枚举
 * 
 * @date 2016年11月12日 下午5:04:51
 */
public enum BizExceptionEnum {
	
    /**
     * 统一异常
     */
    NOT_INVALID_PARAMTER(401, "不合法的参数，参数不能为空！"),
    DATA_EXIST(402,"数据已存在，无法进行相关操作！"),
    EXPORT_FILED_NULL(403,"导出字段不能为空，无法进行相关操作！"),
    LONGITUDE_NULL(404,"经度不能为空，无法进行相关操作！"),
    LATITUDE_NULL(405,"纬度不能为空，无法进行相关操作！"),
    
	/**
	 * 文件上传
	 */
	UPLOAD_ERROR(500,"上传图片出错"),
	
	/**
	 * 文件下载
	 */
	OVER_SIZE_FILE(510,"文件大小超出范围，无法进行相关操作！"),
    FILE_READING_ERROR(511,"文件读取失败，无法进行相关操作！"),
    FILE_NOT_FOUND(512,"文件未找到，无法进行相关操作！"),
    
	/**
	 * 扫码
	 */
	SCAN_REDIRECT_URL_ERROR(401,"扫码异常，请重新扫描或换台设备!"),
	GET_DEVICE_INFO_ERROR(402,"获取设备信息异常，请换台设备!"),
	
	/**
	 * 菜单
	 */
	ORDER_URL_ERROR(401,"服务器异常，页面加载失败..."),
	
	/**
	 * 牌识单元
	 */
	DEVICE_ID_EXIST(401,"设备编号已存在，无法进行相关操作!"),
	DEVICE_ONLINE(533,"投运中的终端只能为在线状态，无法进行相关操作！"),
	
	/**
	 * 厂家
	 */
    VENDER_USEING(401,"该厂家已被使用，无法进行相关操作！"),
    VENDER_NAME_EXIST(402,"该厂家名称已存在，无法进行相关操作！"),
    
    /**
     * 收费站
     */
    STATION_NAME_EXIST(401,"收费站名称已存在，无法进行相关操作!"),
    STATION_NAME_NOT_EXIST(402,"收费站名称不存在，无法进行相关操作!"),
    
    /**
     * 车道
     */
    LANE_NAME_EXIST(401,"车道名称已存在，无法进行相关操作!"),
    NULL_LANE_NAME(402,"车道名称已存在，无法进行相关操作!"),
    NULL_STATION_NAME(403,"收费站名称不能为空，无法进行相关操作!"),
    NOT_FOUND_STATION(403,"找不到所属收费站名称，无法进行相关操作!"),
    
    /**
     * 安全管理，黑名单
     */
    DEVICE_INFO_NULL(401,"该终端信息不存在，无法进行相关操作！"),
    EXIST_BACKLIST(402,"黑名单中已存在该终端序列号，无法进行相关操作！"),
    
    /**
     * 配置管理
     */
    CONF_LIMIT_RANGE(401,"配置限制范围只能为0~100，无法进行相关操作!"),
    CONF_LIMIT_EXCEED(402,"配置下限不能大于上限数值，无法进行相关操作!"),
	
	XML_TO_MAP_ERROR(401,"xml转换对象异常!"),
	;
	
	
	BizExceptionEnum(int code, String message) {
		this.friendlyCode = code;
		this.friendlyMsg = message;
	}
	
	BizExceptionEnum(int code, String message,String urlPath) {
		this.friendlyCode = code;
		this.friendlyMsg = message;
		this.urlPath = urlPath;
	}

	private int friendlyCode;

	private String friendlyMsg;
	
	private String urlPath;

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
