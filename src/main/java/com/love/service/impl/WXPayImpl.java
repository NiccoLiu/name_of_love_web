package com.love.service.impl;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.love.config.WXPayProperties;
import com.love.util.WXPayConstants;
import com.love.util.WXPayConstants.SignType;
import com.love.util.WXPayUtil;


@Component
public class WXPayImpl {

    private static final Logger logger = LoggerFactory.getLogger(WXPayImpl.class);
    private SignType signType;
    private String notifyUrl;

    @Resource
    private WXPayProperties wxPayProperties;

    public WXPayImpl() throws Exception {
        this(null);
    }

    public WXPayImpl(final String notifyUrl) throws Exception {
        this.notifyUrl = notifyUrl;
        this.signType = SignType.HMACSHA256;
    }

    /**
     * 向 Map 中添加 appid、mch_id、nonce_str、sign_type、sign <br>
     * 该函数适用于商户适用于统一下单等接口，不适用于红包、代金券接口
     *
     * @param reqData
     * @return
     * @throws Exception
     */
    public Map<String, String> fillRequestData(Map<String, String> reqData) throws Exception {

        if (!reqData.containsKey("mch_id")) {
            reqData.put("mch_id", wxPayProperties.getMchId());
        }
        if (!reqData.containsKey("appid")) {
            reqData.put("appid", wxPayProperties.getAppid());
        }

        String mchKey = wxPayProperties.getMchKey();
        reqData.put("nonce_str", WXPayUtil.generateUUID());
        reqData.put("sign_type", WXPayConstants.HMACSHA256);
        /*
         * if (SignType.MD5.equals(this.signType)) { reqData.put("sign_type", WXPayConstants.MD5); }
         * else if (SignType.HMACSHA256.equals(this.signType)) { reqData.put("sign_type",
         * WXPayConstants.HMACSHA256); }
         */
        reqData.put("sign", WXPayUtil.generateSignature(reqData, mchKey, this.signType));
        return reqData;
    }

    /**
     * 判断xml数据的sign是否有效，必须包含sign字段，否则返回false。
     *
     * @param reqData 向wxpay post的请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isResponseSignatureValid(Map<String, String> reqData) throws Exception {
        // 返回数据的签名方式和请求中给定的签名方式是一致的
        String mchKey = wxPayProperties.getMchKey();
        return WXPayUtil.isSignatureValid(reqData, mchKey, this.signType);
    }

    /**
     * 判断支付结果通知中的sign是否有效
     *
     * @param reqData 向wxpay post的请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isPayResultNotifySignatureValid(Map<String, String> reqData) throws Exception {
        String signTypeInData = reqData.get(WXPayConstants.FIELD_SIGN_TYPE);
        SignType signType;
        if (signTypeInData == null) {
            signType = SignType.MD5;
        } else {
            signTypeInData = signTypeInData.trim();
            if (signTypeInData.length() == 0) {
                signType = SignType.MD5;
            } else if (WXPayConstants.MD5.equals(signTypeInData)) {
                signType = SignType.MD5;
            } else if (WXPayConstants.HMACSHA256.equals(signTypeInData)) {
                signType = SignType.HMACSHA256;
            } else {
                throw new Exception(String.format("Unsupported sign_type: %s", signTypeInData));
            }
        }
        return WXPayUtil.isSignatureValid(reqData, wxPayProperties.getMchKey(), signType);
    }


    /**
     * 不需要证书的请求
     * 
     * @param urlSuffix String
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 超时时间，单位是毫秒
     * @param readTimeoutMs 超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public String requestWithoutCert(String urlSuffix, Map<String, String> reqData,
            int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String msgUUID = reqData.get("nonce_str");
        String reqBody = WXPayUtil.mapToXml(reqData);
        String mchId = reqData.get("mch_id");
        String resp = this.request(urlSuffix, msgUUID, reqBody, connectTimeoutMs, readTimeoutMs,
                false, mchId);
        return resp;
    }


    /**
     * 需要证书的请求
     * 
     * @param urlSuffix String
     * @param reqData 向wxpay post的请求数据 Map
     * @param connectTimeoutMs 超时时间，单位是毫秒
     * @param readTimeoutMs 超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public String requestWithCert(String urlSuffix, Map<String, String> reqData,
            int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String msgUUID = reqData.get("nonce_str");
        String reqBody = WXPayUtil.mapToXml(reqData);
        String mchId = reqData.get("mch_id");
        if (mchId == null) {
            mchId = reqData.get("mchid");
        }
        String resp = this.request(urlSuffix, msgUUID, reqBody, connectTimeoutMs, readTimeoutMs,
                true, mchId);
        return resp;
    }

    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * 
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    public Map<String, String> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        logger.info("weixin return " + xmlStr);
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        } else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(WXPayConstants.FAIL)) {
            return respData;
        } else if (return_code.equals(WXPayConstants.SUCCESS)) {
            if (this.isResponseSignatureValid(respData)) {
                return respData;
            } else {
                throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
            }
        } else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s",
                    return_code, xmlStr));
        }
    }

    /**
     * 作用：提交刷卡支付<br>
     * 场景：刷卡支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> microPay(Map<String, String> reqData) throws Exception {
        return this.microPay(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：提交刷卡支付<br>
     * 场景：刷卡支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> microPay(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.MICROPAY_URL_SUFFIX;
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 提交刷卡支付，针对软POS，尽可能做成功 内置重试机制，最多60s
     * 
     * @param reqData
     * @return
     * @throws Exception
     */
    public Map<String, String> microPayWithPos(Map<String, String> reqData) throws Exception {
        return this.microPayWithPos(reqData, wxPayProperties.getHttpConnectTimeoutMs());
    }

    /**
     * 提交刷卡支付，针对软POS，尽可能做成功 内置重试机制，最多60s
     * 
     * @param reqData
     * @param connectTimeoutMs
     * @return
     * @throws Exception
     */
    public Map<String, String> microPayWithPos(Map<String, String> reqData, int connectTimeoutMs)
            throws Exception {
        int remainingTimeMs = 60 * 1000;
        long startTimestampMs = 0;
        Map<String, String> lastResult = null;
        Exception lastException = null;

        while (true) {
            startTimestampMs = WXPayUtil.getCurrentTimestampMs();
            int readTimeoutMs = remainingTimeMs - connectTimeoutMs;
            if (readTimeoutMs > 1000) {
                try {
                    lastResult = this.microPay(reqData, connectTimeoutMs, readTimeoutMs);
                    String returnCode = lastResult.get("return_code");
                    if (returnCode.equals("SUCCESS")) {
                        String resultCode = lastResult.get("result_code");
                        String errCode = lastResult.get("err_code");
                        if (resultCode.equals("SUCCESS")) {
                            break;
                        } else {
                            // 看错误码，若支付结果未知，则重试提交刷卡支付
                            if (errCode.equals("SYSTEMERROR") || errCode.equals("BANKERROR")
                                    || errCode.equals("USERPAYING")) {
                                remainingTimeMs =
                                        remainingTimeMs - (int) (WXPayUtil.getCurrentTimestampMs()
                                                - startTimestampMs);
                                if (remainingTimeMs <= 100) {
                                    break;
                                } else {
                                    WXPayUtil.getLogger()
                                            .info("microPayWithPos: try micropay again");
                                    if (remainingTimeMs > 5 * 1000) {
                                        Thread.sleep(5 * 1000);
                                    } else {
                                        Thread.sleep(1 * 1000);
                                    }
                                    continue;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                } catch (Exception ex) {
                    lastResult = null;
                    lastException = ex;
                }
            } else {
                break;
            }
        }

        if (lastResult == null) {
            throw lastException;
        } else {
            return lastResult;
        }
    }



    /**
     * 作用：统一下单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Map<String, String> reqData) throws Exception {
        return this.unifiedOrder(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：统一下单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.UNIFIEDORDER_URL_SUFFIX;
        if (this.notifyUrl != null) {
            reqData.put("notify_url", this.notifyUrl);
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 作用：查询订单<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> orderQuery(Map<String, String> reqData) throws Exception {
        return this.orderQuery(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：查询订单<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据 int
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> orderQuery(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.ORDERQUERY_URL_SUFFIX;
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 作用：撤销订单<br>
     * 场景：刷卡支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> reverse(Map<String, String> reqData) throws Exception {
        return this.reverse(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：撤销订单<br>
     * 场景：刷卡支付<br>
     * 其他：需要证书
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> reverse(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.REVERSE_URL_SUFFIX;
        String respXml = this.requestWithCert(url, this.fillRequestData(reqData), connectTimeoutMs,
                readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 作用：关闭订单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> closeOrder(Map<String, String> reqData) throws Exception {
        return this.closeOrder(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：关闭订单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> closeOrder(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.CLOSEORDER_URL_SUFFIX;
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 作用：申请退款<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> refund(Map<String, String> reqData) throws Exception {
        return this.refund(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：申请退款<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付<br>
     * 其他：需要证书
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> refund(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.REFUND_URL_SUFFIX;
        String respXml = this.requestWithCert(url, this.fillRequestData(reqData), connectTimeoutMs,
                readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 作用：退款查询<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> refundQuery(Map<String, String> reqData) throws Exception {
        return this.refundQuery(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：退款查询<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> refundQuery(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.REFUNDQUERY_URL_SUFFIX;
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 作用：对账单下载（成功时返回对账单数据，失败时返回XML格式数据）<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> downloadBill(Map<String, String> reqData) throws Exception {
        return this.downloadBill(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：对账单下载<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付<br>
     * 其他：无论是否成功都返回Map。若成功，返回的Map中含有return_code、return_msg、data， 其中return_code为`SUCCESS`，data为对账单数据。
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return 经过封装的API返回数据
     * @throws Exception
     */
    public Map<String, String> downloadBill(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.DOWNLOADBILL_URL_SUFFIX;
        String respStr = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs).trim();
        Map<String, String> ret;
        // 出现错误，返回XML数据
        if (respStr.indexOf("<") == 0) {
            ret = WXPayUtil.xmlToMap(respStr);
        } else {
            // 正常返回csv数据
            ret = new HashMap<String, String>();
            ret.put("return_code", WXPayConstants.SUCCESS);
            ret.put("return_msg", "ok");
            ret.put("data", respStr);
        }
        return ret;
    }


    /**
     * 作用：转换短链接<br>
     * 场景：刷卡支付、扫码支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> shortUrl(Map<String, String> reqData) throws Exception {
        return this.shortUrl(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：转换短链接<br>
     * 场景：刷卡支付、扫码支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> shortUrl(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.SHORTURL_URL_SUFFIX;
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 作用：授权码查询OPENID接口<br>
     * 场景：刷卡支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> authCodeToOpenid(Map<String, String> reqData) throws Exception {
        return this.authCodeToOpenid(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }


    /**
     * 作用：授权码查询OPENID接口<br>
     * 场景：刷卡支付
     * 
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> authCodeToOpenid(Map<String, String> reqData, int connectTimeoutMs,
            int readTimeoutMs) throws Exception {
        String url = WXPayConstants.AUTHCODETOOPENID_URL_SUFFIX;
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }

    /**
     * 请求，只请求一次，不做重试
     * 
     * @param domain
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @param useCert 是否使用证书，针对退款、撤销等操作
     * @return
     * @throws Exception
     */
    private String requestOnce(String urlSuffix, String uuid, String data, int connectTimeoutMs,
            int readTimeoutMs, boolean useCert, String mchId) throws Exception {
        BasicHttpClientConnectionManager connManager;
        if (useCert) {
            // 证书
            char[] password = mchId.toCharArray();
            FileInputStream certStream = this.getCertStream(mchId);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certStream, password);
            certStream.close();

            // 实例化密钥库 & 初始化密钥工厂
            KeyManagerFactory kmf =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            // 创建 SSLContext0
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContext, new String[] {"TLSv1"}, null, new DefaultHostnameVerifier());

            connManager =
                    new BasicHttpClientConnectionManager(
                            RegistryBuilder.<ConnectionSocketFactory>create()
                                    .register("http",
                                            PlainConnectionSocketFactory.getSocketFactory())
                                    .register("https", sslConnectionSocketFactory).build(),
                            null, null, null);
        } else {
            connManager =
                    new BasicHttpClientConnectionManager(
                            RegistryBuilder.<ConnectionSocketFactory>create()
                                    .register("http",
                                            PlainConnectionSocketFactory.getSocketFactory())
                                    .register("https",
                                            SSLConnectionSocketFactory.getSocketFactory())
                                    .build(),
                            null, null, null);
        }

        HttpClient httpClient =
                HttpClientBuilder.create().setConnectionManager(connManager).build();

        String url = wxPayProperties.getApiUrl() + urlSuffix;
        logger.debug("wechat data " + data);
        logger.info("wechat url " + url);
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
                .setConnectTimeout(connectTimeoutMs).build();
        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 " + mchId); // TODO:
                                                                          // 很重要，用来检测
                                                                          // sdk
                                                                          // 的使用情况，要不要加上商户信息？
        httpPost.setEntity(postEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");

    }


    /**
     * 获取商户证书内容
     *
     * @return 商户证书内容
     */
    public FileInputStream getCertStream(String mchId) throws Exception {
        return new FileInputStream(wxPayProperties.getCertAddress());
    }

    private String request(String urlSuffix, String uuid, String data, int connectTimeoutMs,
            int readTimeoutMs, boolean useCert, String mchId) throws Exception {
        Exception exception = null;
        try {
            String result = requestOnce(urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs,
                    useCert, mchId);
            return result;
        } catch (Exception ex) {
            logger.info("request exception is:{}", ex);
            exception = ex;
        }
        throw exception;
    }

    public Map<String, String> withdrawalsOrder(HashMap<String, String> reqData) throws Exception {
        return this.withdrawalsOrder(reqData, wxPayProperties.getHttpConnectTimeoutMs(),
                wxPayProperties.getHttpReadTimeoutMs());
    }

    private Map<String, String> withdrawalsOrder(HashMap<String, String> reqData,
            int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url = WXPayConstants.WITHDRAWALS_URL_SUFFIX;
        String respXml = this.requestWithCert(url, this.fillWithdrawalsRequestData(reqData),
                connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }

    private Map<String, String> fillWithdrawalsRequestData(HashMap<String, String> reqData)
            throws Exception {
        if (!reqData.containsKey("mchid")) {
            reqData.put("mchid", wxPayProperties.getMchId());
        }
        if (!reqData.containsKey("mch_appid")) {
            reqData.put("mch_appid", wxPayProperties.getAppid());
        }

        String mchKey = wxPayProperties.getMchKey();
        reqData.put("nonce_str", WXPayUtil.generateUUID());
        // reqData.put("sign_type", WXPayConstants.MD5);
        /*
         * if (SignType.MD5.equals(this.signType)) { reqData.put("sign_type", WXPayConstants.MD5); }
         * else if (SignType.HMACSHA256.equals(this.signType)) { reqData.put("sign_type",
         * WXPayConstants.HMACSHA256); }
         */
        reqData.put("sign", WXPayUtil.generateSignature(reqData, mchKey, SignType.MD5));
        return reqData;
    }

}
