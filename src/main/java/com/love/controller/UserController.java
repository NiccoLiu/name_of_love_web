package com.love.controller;

import com.love.model.User;
import com.love.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2018/3/17.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("/showUser/{id}")
    @ResponseBody
    public User showUser(@PathVariable(value = "id") Integer id){
        User user = this.userService.getUserById(id);
        return user;
    }

    @RequestMapping("/findAll")
    @ResponseBody
    public List<User> findAll(HttpServletRequest request, Model model){
        List<User> list = this.userService.findAll();
        return list;
    }

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(){
        User u1 = new User("ls", "ls", 24);
        User u2 = new User("ww", "ww", 25);
        boolean flag = this.userService.addUser(u1);

        flag = this.userService.addUser(u2);
        return "save sucess";
    }

    @RequestMapping("/batchInsert")
    @ResponseBody
    public String batchInsert(){
        try {
            this.userService.batchAddUser();
            return "save sucess";
        }catch (Exception e){
            e.printStackTrace();
            return "save failed";
        }
    }
}
