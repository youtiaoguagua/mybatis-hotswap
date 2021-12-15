package com.wang.spring.entity;

import lombok.Data;

/**
 * @author 王祥飞
 * @time 2021/12/14 2:01 PM
 */
@Data
public class LoginResp {

    private String token;

    private String name;

    private String avatar;
}
