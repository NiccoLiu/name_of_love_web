package com.love.util;

/**
 * 常量
 */
public class WXPayConstants {

    public enum SignType {
        MD5, HMACSHA256
    }

    public static final String DOMAIN_API = "https://api.mch.weixin.qq.com";
    public static final String DOMAIN_API2 = "https://api2.mch.weixin.qq.com";
    public static final String DOMAIN_APIHK = "https://apihk.mch.weixin.qq.com";
    public static final String DOMAIN_APIUS = "https://apius.mch.weixin.qq.com";



    public static final String WX_RESULT_MSG_KEY = "return_msg";
    public static final String WX_RETURN_CODE_KEY = "return_code";

    public static final String WX_RESULT_CODE_KEY = "result_code";


    public static final String FAIL = "FAIL";
    public static final String SUCCESS = "SUCCESS";
    public static final String HMACSHA256 = "HMAC-SHA256";
    public static final String MD5 = "MD5";

    public static final String FIELD_SIGN = "sign";
    public static final String FIELD_SIGN_TYPE = "sign_type";

    public static final String MICROPAY_URL_SUFFIX = "/pay/micropay";
    public static final String UNIFIEDORDER_URL_SUFFIX = "/pay/unifiedorder";

    public static final String WITHDRAWALS_URL_SUFFIX = "/mmpaymkttransfers/promotion/transfers";

    public static final String ORDERQUERY_URL_SUFFIX = "/pay/orderquery";
    public static final String REVERSE_URL_SUFFIX = "/secapi/pay/reverse";
    public static final String CLOSEORDER_URL_SUFFIX = "/pay/closeorder";
    /**
     * 申请退款
     */
    public static final String REFUND_URL_SUFFIX = "/secapi/pay/refund";
    public static final String REFUNDQUERY_URL_SUFFIX = "/pay/refundquery";
    /**
     * 退款查询
     */
    public static final String DOWNLOADBILL_URL_SUFFIX = "/pay/downloadbill";
    public static final String REPORT_URL_SUFFIX = "/payitil/report";
    public static final String SHORTURL_URL_SUFFIX = "/tools/shorturl";
    public static final String AUTHCODETOOPENID_URL_SUFFIX = "/tools/authcodetoopenid";


    /**
     * 微信app
     */
    public static final Integer WX_PAY_ACCOUNT_TYPE = 1;


    /**
     * 微信公众号
     */
    public static final Integer WX_PUBLIC_PAY_ACCOUNT_TYPE = 5;



    /**
     * 申请扣款
     */
    public static final String PAPPAYAPPLY_URL_SUFFIX = "/pay/pappayapply";


    /**
     * 查询订单
     */
    public static final String PAYPAPORDERQURY_URL_SUFFIX = "/pay/paporderquery";

    /**
     * 申请扣钱之后回调地址
     */
    public static final String PAPAPP_BACK_URL_SUFFIX = "/wxpay/papPayback";
    /**
     * 唤起用户签约页面
     */
    public static final String ENTRUSTWEB_URL_SUFFIX = "/papay/entrustweb";

    /**
     * 唤起用户签约之后回调地址
     */
    public static final String ENTRUSTWEB_NOTIFY_URL_SUFFIX = "/wxpay/entrustwebBack";



    /**
     * 唤起用户签约之后回调地址
     */
    public static final String ENTRUSTWEB_NOTIFY_PUBLIC_URL_SUFFIX = "/wxpay/public/entrustwebBack";

    /**
     * 申请解约
     */
    public static final String DELETECONTRACT_URL_SUFFIX = "/papay/deletecontract";


    /**
     * 查询签约关系
     */
    public static final String QUERYCONTRACT_URL_SUFFIX = "/papay/querycontract";


    public static final String BACK_TO_WEIXIN_SUCCESSED_CODE = "SUCCESS";
    public static final String BACK_TO_WEIXIN_FAILED_CODE = "ERROR";
    public static final String BACK_TO_WEIXIN_SUCCESSED_MSG = "OK";
    public static final String BACK_TO_WEIXIN_FAILED_MSG = "FAILED";



    public static final String PAY_STATUS_SUCCESS = "SUCCESS";// 支付成功
    public static final String PAY_STATUS_REFUND = "REFUND";// 转入退款
    public static final String PAY_STATUS_REVOKED = "REVOKED";// 已撤销（刷卡支付）
    public static final String PAY_STATUS_NOTPAY = "NOTPAY";// 未支付
    public static final String PAY_STATUS_USERPAYING = "USERPAYING";// 用户支付中
    public static final String PAY_STATUS_PAYERROR = "PAYERROR";// 支付失败(其他原因，如银行返回失败)


    public static final String PAY_TYPE_MICROPAY = "MICROPAY";// 刷卡支付

    public static final String PAPPAY_REMARK = "高速费用代扣";


    public static final String SIGN_ING_KEY = "user.sign.";



    /**
     * 刷卡支付
     */
    public static final int BARGAIN_TYPE_MICROPAY = 2;
    public static final int PAY_SUCCESS = 1;
    public static final int REFUND_SUCCESS = 2;
    public static final int REFUND_EXCEPTION = 4;

}

