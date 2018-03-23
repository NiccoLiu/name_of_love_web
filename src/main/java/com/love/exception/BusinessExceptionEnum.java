package com.love.exception;

/**
 * 业务异常枚举值
 * 
 * @author liangcm 规则：
 * 
 */
public enum BusinessExceptionEnum {
	
	
    /**
     * 业务
     */
    NOT_INVALID_PARAMTER(501, "不合法的参数，参数不能为空！"),
    DATA_EXIST(502,"数据已存在，无法进行相关操作！"),
    VENDER_USEING(503,"该厂家已被使用，厂家名称无法进行修改操作！"),
    DEVICE_INFO_NULL(504,"该终端信息不存在，无法进行相关操作！"),
    
    /**
     * 文件导入、导出
     */
    OVER_SIZE_FILE(510,"文件大小超出范围，无法进行相关操作！"),
    FILE_READING_ERROR(511,"文件读取失败，无法进行相关操作！"),
    FILE_NOT_FOUND(512,"文件未找到，无法进行相关操作！"),
    IMPORT_FILED_NULL(513,"导入列表必传字段不能为空，无法进行相关操作！"),
    DEVICE_NAME_NULL(514,"终端名称/序列号不能为空，无法进行相关操作！"),
    DEVICE_TYPE_NULL(515,"终端类型不能为空，无法进行相关操作！"),
    EXPORT_FILED_NULL(516,"导出字段不能为空，无法进行相关操作！"),
    ID_NULL(517,"列表标识为空，无法进行相关操作！"),
    
    ENUM_VALUE_EXIST(701,"枚举值已经存在!"),
    
    /**
     * 终端管理
     */
    DEVICE_ID_EXIST(530,"终端编号已存在，无法进行相关操作！"),
    ERROR_REPORT_SN(531,"故障上报设备编号不能为空，无法进行相关操作！"),
    EXIST_BACKLIST(532,"黑名单中已存在该终端序列号，无法进行相关操作！"),
    DEVICE_ONLINE(533,"投运中的终端只能为在线状态，无法进行相关操作！"),
    NOTICE_TITLE_NULL(534,"公告标题不能为空，无法进行相关操作！"),
    COMPARE_TIME(535,"起始时间不能大于结束时间，无法进行相关操作！"),
    NOTICE_TITLE_EXIST(536,"该公告标题已存在，无法进行相关操作！"),
    DEVICE_TITLE_EXIST(537,"终端已存在该公告标题，无法进行相关操作！"),
    DEVICE_NUM_NULL(538,"所选终端台数为0，无法进行相关操作！"),
    NOTICE_MAX_NUM(539,"最多只能设置6条公告，无法进行相关操作！"),
    MAX_NOTICE_CYCLE(540,"公告推送周期不能超过1小时，无法进行相关操作！"), 
	;


	BusinessExceptionEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	private int code;

	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}




}
