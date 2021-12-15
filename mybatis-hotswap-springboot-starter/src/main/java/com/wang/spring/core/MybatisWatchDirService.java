package com.wang.spring.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 王祥飞
 * @time 2021/12/15 1:16 PM
 */
@Slf4j
public class MybatisWatchDirService {

    /**
     * mybatis文件服务
     */
    private MybatisMapperXmlLoadService mybatisMapperXmlLoadService;

    /**
     * mybatis文件重载服务
     */
    private MybatisMapperXmlFileReloadService mybatisMapperXmlFileReloadService;


    /**
     * 重载次数
     */
    private final AtomicInteger reloadCount = new AtomicInteger(0);

    public MybatisWatchDirService(MybatisMapperXmlLoadService mybatisMapperXmlLoadService, MybatisMapperXmlFileReloadService mybatisMapperXmlFileReloadService) {
        this.mybatisMapperXmlLoadService = mybatisMapperXmlLoadService;
        this.mybatisMapperXmlFileReloadService = mybatisMapperXmlFileReloadService;
    }

    public void init() {
        log.info("init mybatis mapper reload service start");
        mybatisMapperXmlLoadService.getMapperXmlFileDirPaths().forEach(path -> {
            WatchMonitor watchMonitor = WatchUtil.createModify(path, 6, new WatchMybatisXml());
            watchMonitor.start();
        });
    }

    private class WatchMybatisXml extends SimpleWatcher {
        @Override
        public void onModify(WatchEvent<?> event, Path currentPath) {
            String path = currentPath.toString() + "/" + event.context().toString();
            log.info("listener on modify {}", path);
            File file = FileUtil.file(path);
            if (file.isDirectory()) {
                return;
            }
            if (!file.getName().endsWith(".xml")) {
                return;
            }
            boolean result = mybatisMapperXmlFileReloadService.reloadAllSqlSessionFactoryMapper(file.getPath());
            log.info("reload all count ={} current result={} mapper path={} ", file.getPath(), reloadCount.incrementAndGet(), result);
        }
    }
}
