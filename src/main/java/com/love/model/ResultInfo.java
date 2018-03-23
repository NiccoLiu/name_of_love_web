package com.love.model;

import java.io.Serializable;

import com.love.exception.BizExceptionEnum;

/**
 * 返回实体类
 * 
 * @author verne
 */
public class ResultInfo implements Serializable {
    /** 
     *  
     */
    private static final long serialVersionUID = 1L;
    /**
     * 结果码
     */
    protected int code;
    /**
     * 结果详情
     */
    protected String msg;

    /**
     * 结果数据，如果返回错误一般不会返回data，直接为null
     */
    protected Object data;

    public static final ResultInfo RPC_BILLING_ERROR = new ResultInfo(10, "远程调用计费微服务出错！");
    public static final ResultInfo RPC_USER_ERROR = new ResultInfo(11, "远程调用用户微服务出错！");
    public static final ResultInfo DATA_FORMAT_ERROR = new ResultInfo(12, "数据格式错误！");
    public static final ResultInfo JSON_DATA_FORMAT_ERROR = new ResultInfo(13, "JSON格式化错误！");

    public static final ResultInfo ILLEGAL_DATA = new ResultInfo(501, "非法的数据请求！");

    public static final ResultInfo SERVER_ERROR = new ResultInfo(500, "服务器内部错误！");
    public static final ResultInfo NOT_FOUND_PATH = new ResultInfo(404, "接口地址未找到！");

    public ResultInfo() {

    }

    public ResultInfo(BizExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMessage();
    }

    public ResultInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultInfo(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public ResultInfo(String msg) {
        this.code = 30000;
        this.msg = msg;
    }

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultInfo [code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }

}
