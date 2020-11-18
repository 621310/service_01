package com.lyl.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 20996
 */

@RestController
public class AuthTestController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @RequestMapping("/admin/hello")
    public String admin(){
        return "admin";
    }

    @RequestMapping("/db/hello")
    public String dbRequese(){
        return "db";
    }
}
