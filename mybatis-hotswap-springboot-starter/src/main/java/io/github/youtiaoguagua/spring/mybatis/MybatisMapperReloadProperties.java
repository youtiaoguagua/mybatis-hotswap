package io.github.youtiaoguagua.spring.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 王祥飞
 * @time 2021/12/13 2:28 PM
 */
@ConfigurationProperties(prefix = "mybatis.mapper.reload")
@Data
@Validated
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
    @NotEmpty
    private String key = "yyds";

    /**
     * 登录用户名
     */
    @NotEmpty
    private String username = "admin";

    /**
     * 登录密码
     */
    @NotEmpty
    private String password = "123456";

    /**
     * token有效时间
     */
    @NotNull
    private Integer expired = 60 * 24;

    /**
     * mybatis访问路径
     */
    private String urlPrefix = "web/mybatis/reload";
}
