package com.lyl.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class testController {


    @RequestMapping(value = "/test")
    public Map<String,Object> test(){
        Map<String,Object> result = new HashMap<>();
        result.put("code",200);
        result.put("msg","success");
        return result;
    }

    //测试调用网络接口

}
