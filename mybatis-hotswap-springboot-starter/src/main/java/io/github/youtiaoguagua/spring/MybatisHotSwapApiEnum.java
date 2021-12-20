package io.github.youtiaoguagua.spring;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 王祥飞
 * @time 2021/12/20 2:14 PM
 */
@AllArgsConstructor
public enum MybatisHotSwapApiEnum {

    URL_PREFIX("/api/youtiaoguagua/mybatis");

    @Getter
    private final String val;
}
