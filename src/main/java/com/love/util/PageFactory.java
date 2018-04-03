package com.love.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;

/**
 * BootStrap Table默认的分页参数创建
 *
 * 
 * @date 2017-04-05 22:25
 */
public class PageFactory<T> {
    public Page<T> defaultPage() {
        HttpServletRequest request = HttpKit.getRequest();
        int size = 12;
        int current = 0;
        if (!StringUtils.isEmpty(request.getParameter("size"))) {
            size = Integer.valueOf(request.getParameter("size"));
            current = Integer.valueOf(request.getParameter("page"));
        } else {
            size = 60000;
            current = 0;
        }
        String sort = request.getParameter("sort");
        String order = request.getParameter("order");
        if (StringUtils.isEmpty(sort)) {
            Page<T> page = new Page<>(current, size);
            page.setOpenSort(false);
            return page;
        } else {
            Page<T> page = new Page<>(current, size, sort);
            if (Order.ASC.getDes().equals(order)) {
                page.setAsc(true);
            } else {
                page.setAsc(false);
            }
            return page;
        }
    }

    public Page<T> defaultPage(JSONObject paramJson) {
        int limit = 12;
        int current = 1;
        if (!StringUtils.isEmpty(paramJson.getString("size"))) {
            limit = paramJson.getIntValue("size");
            current = paramJson.getIntValue("page");
        } else {
            limit = 60000;
            current = 1;
        }

        String sort = paramJson.getString("sort");
        String order = paramJson.getString("order");
        if (StringUtils.isEmpty(sort)) {
            Page<T> page = new Page<>(current, limit);
            page.setOpenSort(false);
            return page;
        } else {
            Page<T> page = new Page<>(current, limit, sort);
            if (Order.ASC.getDes().equals(order)) {
                page.setAsc(true);
            } else {
                page.setAsc(false);
            }
            return page;
        }
    }
}
