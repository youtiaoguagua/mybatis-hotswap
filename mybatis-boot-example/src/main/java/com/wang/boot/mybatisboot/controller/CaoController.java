package com.wang.boot.mybatisboot.controller;

import com.wang.boot.mybatisboot.dao.CaoDao;
import com.wang.boot.mybatisboot.entity.Cao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author youtiaoguagua
 * @date 2021/12/13 20:38
 */

@RestController
public class CaoController {

    @Autowired
    private CaoDao caoService;

    @GetMapping("test")
    public List<Cao> getCao(){
        List<Cao> caos = caoService.queryLimit();
        return caos;
    }
}
