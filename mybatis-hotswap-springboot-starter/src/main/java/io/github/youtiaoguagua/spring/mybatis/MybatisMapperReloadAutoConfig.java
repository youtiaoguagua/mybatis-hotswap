package io.github.youtiaoguagua.spring.mybatis;

import io.github.youtiaoguagua.spring.core.MybatisMapperXmlLoadService;
import io.github.youtiaoguagua.spring.core.MybatisMapperXmlReloadService;
import io.github.youtiaoguagua.spring.core.MybatisWatchDirService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author 王祥飞
 * @time 2021/12/13 2:27 PM
 */
@Configuration
@ConditionalOnClass(SqlSessionFactory.class)
@EnableConfigurationProperties(MybatisMapperReloadProperties.class)
@ComponentScan(value = {"io.github.youtiaoguagua.spring.controller", "io.github.youtiaoguagua.spring.config", "io.github.youtiaoguagua.spring.intecepter"})
@ConditionalOnProperty(prefix = "mybatis.mapper.reload", value = "enable", havingValue = "true", matchIfMissing = false)
public class MybatisMapperReloadAutoConfig {

    private final MybatisMapperReloadProperties properties;

    public MybatisMapperReloadAutoConfig(MybatisMapperReloadProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MybatisMapperXmlReloadService mybatisMapperXmlFileReloadService(@Autowired(required = false) List<SqlSessionFactory> sqlSessionFactoryList) {
        return new MybatisMapperXmlReloadService(sqlSessionFactoryList);
    }


    @Bean
    public MybatisMapperXmlLoadService mybatisMapperXmlLoadService() {
        MybatisMapperXmlLoadService loadService = new MybatisMapperXmlLoadService(properties.getMapperLocations());
        loadService.init();
        return loadService;
    }

    @Bean
    public MybatisWatchDirService mybatisWatchDirService(@Autowired MybatisMapperXmlReloadService mybatisMapperXmlReloadService, @Autowired MybatisMapperXmlLoadService mybatisMapperXmlLoadService) {
        MybatisWatchDirService mybatisWatchDirService = new MybatisWatchDirService(mybatisMapperXmlLoadService, mybatisMapperXmlReloadService);
        CompletableFuture.runAsync(mybatisWatchDirService::init);
        return mybatisWatchDirService;
    }


}
