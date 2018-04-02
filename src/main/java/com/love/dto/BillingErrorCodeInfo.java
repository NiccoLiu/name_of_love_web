package com.love.dto;

import com.love.model.ResultInfo;

/**
 * 业务错误码
 *
 * @author verne
 */
public class BillingErrorCodeInfo extends ResultInfo {

    /** 
     *  
     */
    private static final long serialVersionUID = 1L;


    /**
     * 计费模块
     */
    public final static ResultInfo AMOUNT_PATTERN_ERROR = new ResultInfo(3000, "充值金额格式错误");
    public final static ResultInfo ORDERID_IS_EMPTY = new ResultInfo(3001, "交易ID不能为空！");
    public final static ResultInfo USERID_IS_EMPTY = new ResultInfo(3002, "userId不能为空！");
    public final static ResultInfo RECEIPT_NOTEXISTS = new ResultInfo(3003, "发票不存在！");

    public static final int WXPAY_ORDER_ERROR_CODE = 3004;
    public final static ResultInfo WXPAY_ORDER_ERROR = new ResultInfo(3004, "微信下单失败！");
    public final static ResultInfo WXPAY_AMOUNT_ERROR = new ResultInfo(3005, "微信下单金额错误！");


}
