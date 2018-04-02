package com.love.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.love.model.ResultInfo;
import com.love.model.User;
import com.love.service.UserService;

/**
 * : Controller类
 * 
 * @author generator
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {


    @Autowired
    private UserService userServiceImpl;


    /**
     * 新增
     * 
     * 
     * @param params jsonstring
     * @return code and msg
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo add(@RequestBody JSONObject params) {
        return userServiceImpl.add(params);
    }


    /**
     * 查询
     * 
     * 
     * @param params jsonstring
     * @return code and entity json string
     */
    @PostMapping("query")
    @ResponseBody
    public ResultInfo query(@RequestBody JSONObject params) {
        ResultInfo resultInfo = new ResultInfo(0, "success");
        User paramEntity = params.toJavaObject(User.class);
        User user = userServiceImpl.query(paramEntity);
        resultInfo.setData(user);
        return resultInfo;
    }



    /**
     * 更新
     * 
     * 
     * @param params jsonstring
     * @return code and msg
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo update(@RequestBody JSONObject params) {
        User user = params.toJavaObject(User.class);
        ResultInfo resultInfo = userServiceImpl.update(user);
        return resultInfo;
    }


    /**
     * 删除
     * 
     * 
     * @param params jsonstring
     * @return code and msg
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo delete(@RequestBody JSONObject params) {
        ResultInfo resultInfo = userServiceImpl.delete(params);
        return resultInfo;
    }


}
