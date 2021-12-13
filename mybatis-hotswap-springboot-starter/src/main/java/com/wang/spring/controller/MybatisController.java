package com.wang.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王祥飞
 * @time 2021/12/13 5:26 PM
 */
@RestController("/api/wang/mybatis")
public class MybatisController {

    @GetMapping("test")
    public String test(){
        return "cao";
    }
}
