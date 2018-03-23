package com.love.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.love.config.MyX509TrustConfig;
import com.love.model.Token;

/**
 * 
 * @author Jekin
 * @date 2017年8月3日上午9:45:07
 */
public class WechatHttpUtil {

    private static Logger Logger = LoggerFactory.getLogger(WechatHttpUtil.class);

    /**
     * 
     * @param requestUrl
     * @param requestMethod
     * @param outputStr 提交的数据
     * @return
     * @throws Exception
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod,
            String outputStr) {
        JSONObject jsonObject = null;
        try {
            // 使用自定义的信任管理器
            TrustManager[] tm = {new MyX509TrustConfig()};
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssFactory = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(ssFactory);
            connection.setDoInput(true); // 设置是否向httpUrlConnection输出
            connection.setDoOutput(true); // 默认为true
            connection.setRequestMethod(requestMethod);

            if (outputStr != null) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 取得输入流
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // 读取响应内容
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            connection.disconnect();
            Logger.info("request to url {} with {} method,the server response is {} ", requestUrl,
                    requestMethod, buffer.toString());
            jsonObject = JSON.parseObject(buffer.toString());
        } catch (ConnectException e) {
            Logger.error("连接超时", e);
        } catch (Exception e) {
            Logger.error("https请求异常", e);
        }

        return jsonObject;
    }

    public static Token getToken(String tokenUrl, String appid, String appsecret) {
        Token token = null;
        String requestUrl = tokenUrl.replace("APPID", appid).replace("APPSECRET", appsecret);
        JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

        if (jsonObject != null) {
            try {
                String tokenValue = jsonObject.getString("access_token");

                token = new Token();
                token.setAccessToken(tokenValue);
                token.setExperesIn(jsonObject.getIntValue("expires_in"));
            } catch (JSONException e) {
                token = null;
                Logger.error("获取token失败errcode:{}errmsg:{}", jsonObject.getIntValue("errcode"),
                        jsonObject.getString("errmsg"));
            }

        }
        return token;
    }

    public static String requestUrl(String requestUrl, String requestMethod, String reqBody) {
        String reqs = null;
        try {
            URL httpUrl = new URL(requestUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setConnectTimeout(10 * 1000);
            httpURLConnection.setReadTimeout(10 * 1000);
            httpURLConnection.connect();
            if (reqBody != null) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(reqBody.getBytes("UTF-8"));
                if (outputStream != null) {
                    outputStream.close();
                }
            }

            // 获取内容
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            reqs = stringBuffer.toString();

            if (stringBuffer != null) {
                bufferedReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (ConnectException e) {
            Logger.debug("连接超时", e);
        } catch (Exception e) {
            Logger.debug("https请求异常", e);
        }
        return reqs;
    }

    /**
     * 获取unix时间，从1970-01-01 00:00:00开始的秒数
     * 
     * @param date
     * @return long
     */
    public static long getUnixTime(Date date) {
        if (null == date) {
            return 0;
        }

        return date.getTime() / 1000;
    }

    /**
     * 
     * @author Jekin
     * @date 2017年8月10日下午3:27:40
     * @description 生成随机数字和字母
     * @param length
     * @return
     */
    public static String getStringRandom() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

}
