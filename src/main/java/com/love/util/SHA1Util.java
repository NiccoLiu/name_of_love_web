package com.love.util;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHA1Util {
	
	private static final Logger logger = LoggerFactory.getLogger(SHA1Util.class);
	
	/** 
     * SHA1 安全加密算法 
     * @param maps 参数key-value map集合 
     * @return 
     * @throws DigestException  
     */  
    @SuppressWarnings("rawtypes")
	public static String sha1Sign(SortedMap<Object,Object> parameters) {  
        //获取信息摘要 - 参数字典排序后字符串  
    	if(parameters == null){
    		return null;
    	}
    	
        StringBuffer sb = new StringBuffer();  
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）  
        Iterator it = es.iterator();  
        while(it.hasNext()) {  
            Map.Entry entry = (Map.Entry)it.next();  
            String k = (String)entry.getKey();  
            Object v = entry.getValue();  
            if(null != v && !"".equals(v)   
                    && !"sign".equals(k) && !"key".equals(k)) {  
                sb.append(k + "=" + v + "&");  
            }  
        }
        String params = sb.toString();
        String decrypt = params.substring(0, params.length() - 1);  
        try {  
            //指定sha1算法  
            MessageDigest digest = MessageDigest.getInstance("SHA-1");  
            digest.update(decrypt.getBytes());  
            //获取字节数组  
            byte messageDigest[] = digest.digest();  
            // Create Hex String  
            StringBuffer hexString = new StringBuffer();  
            // 字节数组转换为 十六进制 数  
            for (int i = 0; i < messageDigest.length; i++) {  
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);  
                if (shaHex.length() < 2) {  
                    hexString.append(0);  
                }  
                hexString.append(shaHex);  
            }  
            return hexString.toString();  
  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
            logger.warn("SHA1签名错误！" + e.getMessage());
            return null;
        }  
    } 
	
}
