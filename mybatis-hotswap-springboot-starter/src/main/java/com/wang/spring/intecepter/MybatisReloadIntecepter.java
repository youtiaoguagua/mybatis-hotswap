package com.wang.spring.intecepter;

import com.wang.spring.mybatis.MybatisMapperReloadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author 王祥飞
 * @time 2021/12/15 11:11 AM
 */
@Component
public class MybatisReloadIntecepter implements HandlerInterceptor {

    @Autowired
    private MybatisMapperReloadProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (Objects.equals(request.getRequestURI(), "/api/wang/mybatis/login") && Objects.equals(request.getMethod(), "POST")) {
            return true;
        }
        String token = request.getHeader("X-Token");
        return Objects.equals(properties.getToken(), token);
    }
}
