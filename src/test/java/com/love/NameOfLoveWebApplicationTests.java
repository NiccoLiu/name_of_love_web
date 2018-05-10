package com.love;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.love.config.WechatProperties;
import com.love.model.MenuUtil;
import com.love.model.Token;
import com.love.service.RedisService;
import com.love.util.QRCodeUtil;
import com.love.util.WechatHttpUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NameOfLoveWebApplicationTests {
    private final static Logger logger =
            LoggerFactory.getLogger(NameOfLoveWebApplicationTests.class);
    @Resource
    WechatProperties wechatProp;

    @Autowired
    RedisService redisService;

    @Test
    public void contextLoads() {}

    @Test
    public void generate() {

        String accessToken = null;
        if (accessToken == null || "".equals(accessToken)) {
            Token token = WechatHttpUtil.getToken(wechatProp.getTokenUrl(), wechatProp.getAppid(),
                    wechatProp.getAppsecret());
            accessToken = token.getAccessToken();
            redisService.set("token", accessToken, 4800);
        }

        /**
         * 创建菜单 MENU_CREATE_URL 自定义菜单 PERSONAL_MENU_URL 个性化菜单
         */
        boolean result = MenuUtil.createMenu(wechatProp.getAppid(), wechatProp.getMenuCreateUrl(),
                accessToken);
        if (result) {
            logger.info("菜单创建成功");
        } else {
            logger.info("菜单创建失败");
        }

    }

    // 生成二维码
    @Test
    public void testEncode() throws FileNotFoundException, Exception {
        String dir = "C:/Users/Administrator/Desktop/1.jpg";
        // String content =
        // "https://iot.1000mob.com/cloud/api/scan/machine?corp=志高&model=DH_001&devsn=3423&module=123321";
        // String content =
        // "https://iot.1000mob.com/sharing/api/scan/machine?corp=志高&model=DH_001&devsn=3423&module=123321";
        String content = "http://iot.1000mob.com/dev/config/menu/index";
        File file = new File(dir);
        QRCodeUtil.encode(content, null, new FileOutputStream(file), false);

    }

    @Test
    public void testTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        long test1 = date.getTime();
        String time = simpleDateFormat.format(date);

        long epoch = 0;
        try {
            epoch = simpleDateFormat.parse(time).getTime() / 1000;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(test1 + "===============" + epoch);
    }

    @Test
    public void monthSpace() throws ParseException {
        Date earlydate = new Date();
        Date latedate = new Date();
        DateFormat df = DateFormat.getDateInstance();
        try {
            earlydate = df.parse("2018-02-16");
            latedate = df.parse("2018-04-16");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = daysBetween(earlydate, latedate);
        System.out.println(days);
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
