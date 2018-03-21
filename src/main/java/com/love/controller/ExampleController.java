package com.love.controller;

import com.love.model.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2018/3/16.
 */
@Controller
public class ExampleController {

    @RequestMapping(value = "/show")
    @ResponseBody
    public Example show(){
        Example e = new Example(Long.valueOf(1), "zhangshan", 23);
        return e;
    }

    @RequestMapping(value = "/index")
    @ResponseBody
    public String index(){
        return "你好，Spring Boot";
    }

    @RequestMapping(value="/test")
    public String test(){
        return "test";
    }

    @RequestMapping(value="/view")
    public String view(){
        return "test";
    }

    @RequestMapping(value="/test1")
    public String test1(){
        return "demo/test1";
    }
}
