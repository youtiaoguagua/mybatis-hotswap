package com.wang.spring.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 王祥飞
 * @time 2021/12/14 11:40 AM
 */
@Data
public class LoginReq implements Serializable {

    private String username;

    private String password;
}
