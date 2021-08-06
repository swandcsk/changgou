package com.itheima.controller;


import com.itheima.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping(value = "/test")
public class TestController {

    /**
     * 基本案例
     * @return
     */
    @GetMapping(value = "/hello")
    public String hello(Model model){
        model.addAttribute("message","hello thymeleaf!");

        //创建一个List<User>,并将List<User>存入到Model中,页面使用Thymeleaf标签展示
        List<User> users = new ArrayList<>();
        users.add(new User(1,"张三","深圳"));
        users.add(new User(2,"李四","北京"));
        users.add(new User(3,"王五","武汉"));
        model.addAttribute("users",users);

        //Map定义
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("No","123");
        dataMap.put("Address","深圳");
        model.addAttribute("dataMap",dataMap);


        //日期
        model.addAttribute("now",new Date());


        //if条件
        model.addAttribute("age",22);
        return "demo1";
    }
}
