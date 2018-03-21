package com.love.service.impl;

import com.love.mapper.UserMapper;
import com.love.model.User;
import com.love.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2018/3/17.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    public User getUserById(int userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public boolean addUser(User record){
        boolean result = false;
        try {
            userMapper.insert(record);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    @Transactional
    public void batchAddUser() {
        User u1 = new User("ls", "ls", 24);
        User u2 = new User("ww", "ww", 25);
        userMapper.insert(u1);
        if(1==1)
            throw new RuntimeException("操作失败");
        userMapper.insert(u2);
    }

}
