package com.love.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.love.mapper.UserDAO;
import com.love.model.ResultInfo;
import com.love.model.User;
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
        ResultInfo resultInfo = new ResultInfo(0, "success");
        return resultInfo;
    }

}
