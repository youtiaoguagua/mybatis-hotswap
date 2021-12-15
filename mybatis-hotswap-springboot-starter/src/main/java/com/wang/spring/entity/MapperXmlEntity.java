package com.wang.spring.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author 王祥飞
 * @time 2021/12/14 11:01 AM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapperXmlEntity implements Serializable {

    private String name;

    private String path;

    private String xml;


    public boolean check(){
        return !Objects.isNull(name) && !Objects.isNull(path) && !Objects.isNull(xml);
    }
}
