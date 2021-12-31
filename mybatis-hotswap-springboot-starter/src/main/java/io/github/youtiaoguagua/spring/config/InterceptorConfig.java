package io.github.youtiaoguagua.spring.config;

import io.github.youtiaoguagua.spring.MybatisHotSwapApiEnum;
import io.github.youtiaoguagua.spring.intecepter.MybatisReloadIntecepter;
import io.github.youtiaoguagua.spring.mybatis.MybatisMapperReloadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

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
                .addPathPatterns(MybatisHotSwapApiEnum.URL_PREFIX.getVal() + "/**");
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
        registry.addViewController(properties.getUrlPrefix()).setViewName("forward:" + "/index.html");
    }

    // 兼容spring4.x

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }
}
