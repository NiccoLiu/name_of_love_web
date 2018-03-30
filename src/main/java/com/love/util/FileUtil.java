package com.love.util;

import java.io.File;

import org.springframework.util.StringUtils;

public class FileUtil {


    /**
     * 判断路径是否存在，如果不存在则创建
     * 
     * 
     * @param dir
     */
    public static void mkdirs(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return;
        }

        File file = new File(dir);
        if (file.isDirectory()) {
            return;
        } else {
            file.mkdirs();
        }
    }
}

