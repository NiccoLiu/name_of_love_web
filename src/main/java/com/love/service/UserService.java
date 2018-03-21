package com.love.service;

import com.love.model.User;

import java.util.List;

/**
 * Created by Administrator on 2018/3/17.
 */
public interface UserService {
    public User getUserById(int userId);
    boolean addUser(User record);
    List<User> findAll();
    void batchAddUser();
}
