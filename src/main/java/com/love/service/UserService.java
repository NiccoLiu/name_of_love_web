package com.love.service;


import com.alibaba.fastjson.JSONObject;
import com.love.model.ResultInfo;
import com.love.model.User;

/**
 * :业务接口类
 * 
 * @author generator
 */
public interface UserService {

    /**
     * 新增
     * 
     * 
     * @param params jsonstring from entity
     * @return code and msg
     */
    ResultInfo add(JSONObject params);


    /**
     * 查询
     * 
     * 
     * @param user jsonstring to entity
     * @return code and entity json string
     */
    User query(User user);



    /**
     * 更新
     * 
     * @param user jsonstring to entity
     * @return code and msg
     */
    ResultInfo update(User user);


    /**
     * 删除
     * 
     * @param params jsonstring from delete criteria
     * @return code and msg
     */
    ResultInfo delete(JSONObject params);


    ResultInfo shareInfo(JSONObject params);


    ResultInfo synchroStep(JSONObject params);


    ResultInfo bindPhone(JSONObject params);


    ResultInfo findPhone(JSONObject params);
}
