package com.love.service;

import com.alibaba.fastjson.JSONObject;
import com.love.model.ResultInfo;

public interface WechatConfigService {

    ResultInfo configJssdk(JSONObject jsonObject);

    ResultInfo getUserInfoByCode(JSONObject jsonObject);


}
