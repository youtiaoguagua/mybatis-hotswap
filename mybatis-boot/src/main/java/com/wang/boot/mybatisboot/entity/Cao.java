package com.wang.boot.mybatisboot.entity;

import java.io.Serializable;

/**
 * (Cao)实体类
 *
 * @author makejava
 * @since 2021-12-13 20:30:48
 */
public class Cao implements Serializable {
    private static final long serialVersionUID = -73388150110012854L;
    
    private Integer id;
    
    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

