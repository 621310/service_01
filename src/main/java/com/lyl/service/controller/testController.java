package com.lyl.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class testController {


    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @RequestMapping(value = "/test")
    public Map<String,Object> test(){
        Map<String,Object> result = new HashMap<>();
        result.put("code",200);
        result.put("msg","success");
        String key = "zszxz";
        String value = "知识追寻者";
        redisTemplate.opsForValue().set(key, value);
        return result;
    }

    //测试调用网络接口

}
