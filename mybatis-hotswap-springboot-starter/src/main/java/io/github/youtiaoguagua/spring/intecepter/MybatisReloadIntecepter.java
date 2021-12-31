package io.github.youtiaoguagua.spring.intecepter;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import io.github.youtiaoguagua.spring.MybatisHotSwapApiEnum;
import io.github.youtiaoguagua.spring.entity.Response;
import io.github.youtiaoguagua.spring.mybatis.MybatisMapperReloadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
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
        if (Objects.equals(request.getRequestURI(), MybatisHotSwapApiEnum.URL_PREFIX.getVal() + "/login") && Objects.equals(request.getMethod(), "POST")) {
            return true;
        }
        String token = request.getHeader("X-Token");

        JWT jwt = JWTUtil.parseToken(token);
        boolean verify = jwt.setKey(properties.getKey().getBytes()).verify();
        boolean validate = jwt.validate(0);

        if (!(verify && validate)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            try (PrintWriter writer = response.getWriter()) {
                String res = JSONUtil.toJsonStr(Response.fail("token验证失败过期！"));
                writer.println(res);
            }
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
