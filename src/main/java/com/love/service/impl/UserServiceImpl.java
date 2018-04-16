package com.love.service.impl;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidParameterSpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.xfire.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.love.config.WechatProperties;
import com.love.mapper.OrderDAO;
import com.love.mapper.SignDAO;
import com.love.mapper.UserDAO;
import com.love.model.OrderDetail;
import com.love.model.ResultInfo;
import com.love.model.Sign;
import com.love.model.User;
import com.love.service.RedisService;
import com.love.service.UserService;

/**
 * :业务接口实现类
 * 
 * 
 * @author generator
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private SignDAO signDAO;
    @Autowired
    private RedisService redisService;
    @Autowired
    private WechatProperties wechatProperties;

    @Autowired
    private OrderDAO orderDAO;

    @Override
    public ResultInfo add(JSONObject params) {
        logger.debug("add User ,the params is {}", params);
        ResultInfo resultInfo = new ResultInfo(0, "success");
        User user = params.toJavaObject(User.class);
        userDAO.insert(user);
        return resultInfo;
    }

    @Override
    public User query(User param) {
        logger.debug("query User by {}", param);
        User user = userDAO.selectOne(param);
        return user;
    }

    @Override
    public ResultInfo update(User user) {
        logger.debug("update User ,the entity is {}", user);
        ResultInfo resultInfo = new ResultInfo(0, "success");
        userDAO.updateById(user);
        return resultInfo;
    }

    @Override
    public ResultInfo delete(JSONObject params) {
        logger.debug("delete User by params {}", params);
        Long id = params.getLong("id");
        ResultInfo resultInfo = new ResultInfo(0, "success");
        userDAO.deleteById(id);
        return resultInfo;
    }

    @Override
    public ResultInfo shareInfo(JSONObject params) {
        logger.debug("shareInfo User by params {}", params);
        String sessionKey = params.getString("sessionKey");
        String openId = redisService.get(sessionKey);
        Sign sign = new Sign();
        sign.setOpenid(openId);
        sign.setCreateTime(new Date());
        Sign result = signDAO.findSign(sign);
        User user = new User();
        user.setOpenid(openId);
        user = userDAO.selectOne(user);
        if (result == null) {
            Sign signNew = new Sign();
            signNew.setOpenid(openId);
            signDAO.insert(signNew);
            Map<String, Object> columnMap = new HashMap<>(3);
            columnMap.put("openid", openId);
            columnMap.put("pay_type", 1);
            List<OrderDetail> lists = orderDAO.selectByMap(columnMap);
            BigDecimal tenDecimal = new BigDecimal("10");
            BigDecimal fiveDecimal = new BigDecimal("5");
            for (Iterator<OrderDetail> iterator = lists.iterator(); iterator.hasNext();) {
                OrderDetail orderDetail = (OrderDetail) iterator.next();
                int days = daysBetween(orderDetail.getCreateTime(), new Date());
                BigDecimal cashBack = orderDetail.getCashback().add(tenDecimal);
                BigDecimal cashFiveBack = orderDetail.getCashback().add(fiveDecimal);
                if (days <= 90 && orderDetail.getAmount().doubleValue() >= cashBack.doubleValue()) {
                    user.setBalance(user.getBalance().add(tenDecimal));
                    user.setCashBack(user.getCashBack().add(tenDecimal));
                    user.setCashToday(user.getCashToday().add(tenDecimal));
                    userDAO.updateById(user);
                    orderDetail.setCashback(cashBack);
                    orderDAO.updateById(orderDetail);
                } else if (days <= 180
                        && orderDetail.getAmount().doubleValue() >= cashFiveBack.doubleValue()) {
                    user.setBalance(user.getBalance().add(fiveDecimal));
                    user.setCashBack(user.getCashBack().add(fiveDecimal));
                    user.setCashToday(user.getCashToday().add(fiveDecimal));
                    userDAO.updateById(user);
                    orderDetail.setCashback(cashFiveBack);
                    orderDAO.updateById(orderDetail);
                }
            }

        }

        Map<String, String> dataMap = new HashMap<>(3);
        dataMap.put("url",
                wechatProperties.getTemplateUrl() + File.separator + "config/menu/" + sessionKey);
        dataMap.put("message", "今天我又领了" + user.getCashToday() + "元现金");
        ResultInfo resultInfo = new ResultInfo(0, "success");
        resultInfo.setData(dataMap);
        return resultInfo;
    }

    @Override
    public ResultInfo synchroStep(JSONObject params) {
        logger.debug("synchroStep by params {}", params);
        ResultInfo resultInfo = new ResultInfo(0, "success");
        String rdSession = params.getString("rdSession");
        String session = redisService.get(rdSession);
        JSONObject sessionJson = JSONObject.parseObject(session);
        String unionid = sessionJson.getString("unionId");
        String sessionKey = sessionJson.getString("sessionkey");
        String encryptedData = params.getString("encryptedData");
        String iv = params.getString("iv");
        JSONObject userJsonInfo = getStepInfo(encryptedData, sessionKey, iv);
        logger.info("encryptedData data is {}", userJsonInfo);
        if (userJsonInfo != null) {
            JSONArray jsonArray = userJsonInfo.getJSONArray("stepInfoList");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String time = simpleDateFormat.format(date);
            long epoch = 0;
            try {
                epoch = simpleDateFormat.parse(time).getTime() / 1000;
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }
            JSONObject today = jsonArray.getJSONObject(30);
            long dateLong = today.getLongValue("timestamp");
            long step = 0;
            if (epoch == dateLong) {
                step = today.getLongValue("step");
            } else {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject everyDay = jsonArray.getJSONObject(i);
                    if (epoch == everyDay.getLong("timestamp")) {
                        step = everyDay.getLongValue("step");
                    }
                }
            }
            if (unionid != null) {
                userDAO.updateStep(unionid, step);
            }
        }
        resultInfo.setData(userJsonInfo);
        return resultInfo;
    }

    public JSONObject getStepInfo(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);

        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JSONObject.parseObject(result);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final int daysBetween(Date early, Date late) {

        java.util.Calendar calst = java.util.Calendar.getInstance();
        java.util.Calendar caled = java.util.Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        // 设置时间为0时
        calst.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calst.set(java.util.Calendar.MINUTE, 0);
        calst.set(java.util.Calendar.SECOND, 0);
        caled.set(java.util.Calendar.HOUR_OF_DAY, 0);
        caled.set(java.util.Calendar.MINUTE, 0);
        caled.set(java.util.Calendar.SECOND, 0);
        // 得到两个日期相差的天数
        int days = ((int) (caled.getTime().getTime() / 1000)
                - (int) (calst.getTime().getTime() / 1000)) / 3600 / 24;

        return days;
    }
}
