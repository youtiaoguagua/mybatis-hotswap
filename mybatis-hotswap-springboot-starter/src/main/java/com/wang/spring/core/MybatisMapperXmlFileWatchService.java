package com.wang.spring.core;

import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryWatcher;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 王祥飞
 * @time 2021/12/13 2:38 PM
 */
@Slf4j
public class MybatisMapperXmlFileWatchService {

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();


    public MybatisMapperXmlFileWatchService(MybatisMapperXmlLoadService mybatisMapperXmlLoadService) {
        this.mybatisMapperXmlLoadService = mybatisMapperXmlLoadService;
    }

    /**
     * mybatis文件配置路径
     */
    private MybatisMapperXmlLoadService mybatisMapperXmlLoadService;


    /**
     * 计数器
     */
    private final AtomicInteger reloadCount = new AtomicInteger(0);

    /**
     * xml 文件
     */
    private static final String XML_PATTERN = "*.xml";

    /**
     * mybatis mapper xml 重新加载服务
     */
    @Setter
    private MybatisMapperXmlFileReloadService mybatisMapperXmlFileReloadService;

    /**
     * 文件监听服务
     */
    private DirectoryWatcher watcher;

    /**
     * 启动监听
     */
    public void initWatchService() {
        log.info("init mybatis mapper reload service begin");
        if (watcher != null) {
            return;
        }
        // 扫描xml路径
        Set<Path> mapperXmlFileDirPaths = mybatisMapperXmlLoadService.getMapperXmlFileDirPaths();
        if (CollectionUtils.isEmpty(mapperXmlFileDirPaths)) {
            log.warn("not found mapper xml in {}", Arrays.stream(mybatisMapperXmlLoadService.getLocations()));
            return;
        }
        this.startMapperWatchService(mapperXmlFileDirPaths);
        log.info("init mybatis mapper reload service success");

    }

    /**
     * 启动监听 mapper xml 变化的服务
     *
     * @param mapperXmlFileDirPaths
     * @throws IOException
     */
    private void startMapperWatchService(Set<Path> mapperXmlFileDirPaths) {
        try {
            watcher = DirectoryWatcher.builder()
                    .paths(new ArrayList<>(mapperXmlFileDirPaths))
                    .listener(event -> {
                        if (DirectoryChangeEvent.EventType.MODIFY.equals(event.eventType())) {
                            if (event.isDirectory()) {
                                return;
                            }
                            Path path = event.path();
                            String fullPath = path.toString();
                            if (!PatternMatchUtils.simpleMatch(XML_PATTERN, fullPath)) {
                                return;
                            }
                            boolean result = mybatisMapperXmlFileReloadService.reloadAllSqlSessionFactoryMapper(fullPath);
                            log.info("reload all count ={} current result={} mapper path={} ", path, reloadCount.incrementAndGet(), result);
                        }
                    })
                    .fileHashing(true)
                    .build();

            Thread watchThread = new Thread(this::runWatchService, "WatchService mybatis mapper xml reload");
            watchThread.setDaemon(true);
            watchThread.start();
        } catch (Exception e) {
            log.error("startMapperWatchService error", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            watcher.close();
        } catch (Exception e) {
            log.debug("DirectoryWatcher got an exception while close!", e);
        }
    }

    /**
     * 启动监听 变更事件
     */
    private void runWatchService() {
        try {
            if (watcher != null) {
                watcher.watch();
            }
        } catch (Exception e) {
            log.debug("DirectoryWatcher got an exception while watching!", e);
        }
    }
}
