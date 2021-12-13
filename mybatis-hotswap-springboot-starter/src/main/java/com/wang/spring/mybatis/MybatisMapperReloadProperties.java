package com.wang.spring.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 王祥飞
 * @time 2021/12/13 2:28 PM
 */
@ConfigurationProperties(prefix = "mybatis.reload.mapper.reload")
@Data
public class MybatisMapperReloadProperties {

    /**
     * 是否开启mybatis更新
     */
    private boolean enable = false;

    /**
     * mapper文件目录
     */
    private String[] mapperLocations;
}
