package com.wang.spring.config;

import com.wang.spring.intecepter.MybatisReloadIntecepter;
import com.wang.spring.mybatis.MybatisMapperReloadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author 王祥飞
 * @time 2021/12/14 5:37 PM
 */
@Configuration
@EnableWebMvc
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private MybatisMapperReloadProperties properties;

    @Autowired
    private MybatisReloadIntecepter mybatisReloadIntecepter;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mybatisReloadIntecepter)
                .addPathPatterns("/api/wang/mybatis" + "/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String urlPrefix = getUrlPrefix();
        registry.addResourceHandler(urlPrefix + "/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "web/");
        registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "web/");
    }

    private String getUrlPrefix() {
        String urlPrefix = properties.getUrlPrefix();
        if (urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
        }
        return urlPrefix;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String urlPrefix = getUrlPrefix();
        WebMvcConfigurer.super.addViewControllers(registry);
        registry.addViewController(urlPrefix).setViewName("forward:" + "/index.html");
    }
}
