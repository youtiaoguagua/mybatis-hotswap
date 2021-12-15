package com.wang.spring.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 王祥飞
 * @time 2021/12/13 2:28 PM
 */
@ConfigurationProperties(prefix = "mybatis.mapper.reload")
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

    /**
     * 登录token
     */
    private String token = "yyds";

    /**
     * 登录用户名
     */
    private String username = "admin";

    /**
     * 登录密码
     */
    private String password = "123456";

    /**
     * mybatis访问路径
     */
    private String urlPrefix = "web/mybatis/reload";
}
