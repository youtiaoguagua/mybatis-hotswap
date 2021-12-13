package com.wang.spring.core;

import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.ResourceUtils;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author 王祥飞
 * @time 2021/12/13 2:38 PM
 */
@Slf4j
public class MybatisMapperXmlFileWatchService {

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();


    public MybatisMapperXmlFileWatchService(String[] mybatisMapperLocals) {
        this.mybatisMapperLocals = mybatisMapperLocals;
    }

    /**
     * mybatis文件配置路径
     */
    private final String[] mybatisMapperLocals;


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
    private MybatisMapperXmlFileReloadService mybatisMapperXmlFileReloadService;

    /**
     * 文件监听服务
     */
    private DirectoryWatcher watcher;

    /**
     * mybatis  mapper xml 的文件路径
     */
    private Resource[] mapperLocations;

    /**
     * 启动监听
     */
    public void initWatchService() {
        log.info("init mybatis mapper reload service begin");
        if (watcher != null) {
            return;
        }
        // 扫描xml路径
        this.scanMapperXml();
        Set<Path> mapperXmlFileDirPaths = this.getWatchMapperXmlFileDirPaths();
        if (CollectionUtils.isEmpty(mapperXmlFileDirPaths)) {
            log.warn("not found mapper xml in {}", mapperLocations);
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

    /**
     * 获取需要被监听的 mapper的父文件dir路径
     *
     * @return
     */
    private Set<Path> getWatchMapperXmlFileDirPaths() {
        Resource[] resources = this.mapperLocations;
        if (resources.length == 0) {
            return Collections.emptySet();
        }
        Set<Path> parentDirSet = new HashSet<>(5);
        for (Resource resource : resources) {
            try {
                if (!resource.exists() || ResourceUtils.isJarURL(resource.getURL())) {
                    continue;
                }
                if (ResourceUtils.isFileURL(resource.getURL())) {
                    File file = resource.getFile();
                    String parentDir = file.getParent();
                    parentDirSet.add(Paths.get(parentDir));
                }
            } catch (Exception e) {
                log.warn("getWatchMapperXmlFileDirPaths error resource={}", resource, e);
            }
        }
        return parentDirSet;
    }

    private void scanMapperXml() {
        this.mapperLocations = Stream.of(Optional.ofNullable(mybatisMapperLocals).orElse(new String[0]))
                .flatMap(location -> {
                    try {
                        return Stream.of(new PathMatchingResourcePatternResolver().getResources(location));
                    } catch (IOException e) {
                        log.warn("scan mapper xml error location={}", location, e);
                    }
                    return null;
                }).filter(Objects::nonNull).toArray(Resource[]::new);
    }

    /**
     * 获取xml文件路径
     */
    //private void scanMapperXml() {
    //    this.mapperLocations = Stream.of(Optional.ofNullable(mybatisMapperLocals).orElse(new String[0]))
    //            .flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
    //}

    //private Resource[] getResources(String location) {
    //    try {
    //        return resourceResolver.getResources(location);
    //    } catch (IOException e) {
    //        return new Resource[0];
    //    }
    //}


    @PreDestroy
    public void destroy() {
        try {
            watcher.close();
        } catch (Exception e) {
            log.debug("DirectoryWatcher got an exception while close!", e);
        }
    }

    public void setMybatisMapperXmlFileReloadService(MybatisMapperXmlFileReloadService mybatisMapperXmlFileReloadService) {
        this.mybatisMapperXmlFileReloadService = mybatisMapperXmlFileReloadService;
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
