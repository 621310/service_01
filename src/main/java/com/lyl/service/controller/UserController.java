package com.lyl.service.controller;


import com.lyl.service.entity.User;
import com.lyl.service.mapper.UserMapper;
import com.lyl.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/test001")
    public Map<String,Object> test001(@RequestParam Map<String,Object> param){
        System.out.println(param);
        Map<String,Object> map = new HashMap<>();
        map.put("username",param.get("username"));
        return map;
    }
}
