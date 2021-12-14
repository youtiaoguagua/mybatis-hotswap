package com.wang.spring.core;

import cn.hutool.core.io.file.FileReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author 王祥飞
 * @time 2021/12/14 10:15 AM
 */
@Slf4j
public class MybatisMapperXmlLoadService {

    @Getter
    private String[] locations;

    @Getter
    private Resource[] mappingResources;

    @Getter
    private Set<Path> mapperXmlFileDirPaths;

    @Getter
    private HashMap<String, String> mapperXmlFileStream = new HashMap<>();

    @Getter
    private HashMap<String, String> mapperXmlPath = new HashMap<>();

    private boolean init = false;

    public MybatisMapperXmlLoadService(String[] locations) {
        this.locations = locations;
    }

    public void init() {
        if (!init) {
            scanMapperXml();
            getWatchMapperXmlFileDirPaths();
        }
        init = true;
    }


    private void scanMapperXml() {
        this.mappingResources = Stream.of(Optional.ofNullable(locations).orElse(new String[0]))
                .flatMap(location -> {
                    try {
                        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(location);
                        return Stream.of(resources);
                    } catch (IOException e) {
                        log.warn("scan mapper xml error location={}", location, e);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .peek(resource -> {
                    try {
                        File file = resource.getFile();
                        FileReader fileReader = new FileReader(file);
                        String xml = fileReader.readString();
                        mapperXmlFileStream.put(file.getPath(), xml);
                        mapperXmlPath.put(file.getName(), file.getPath());
                    } catch (IOException e) {
                        log.warn("scan mapper xml error resource={}", resource, e);
                    }
                }).toArray(Resource[]::new);
    }

    /**
     * 获取需要被监听的 mapper的父文件dir路径
     *
     * @return
     */
    private Set<Path> getWatchMapperXmlFileDirPaths() {
        Resource[] resources = this.mappingResources;
        if (resources.length == 0) {
            return Collections.emptySet();
        }
        mapperXmlFileDirPaths = new HashSet<>(5);
        for (Resource resource : resources) {
            try {
                if (!resource.exists() || ResourceUtils.isJarURL(resource.getURL())) {
                    continue;
                }
                if (ResourceUtils.isFileURL(resource.getURL())) {
                    File file = resource.getFile();
                    String parentDir = file.getParent();
                    mapperXmlFileDirPaths.add(Paths.get(parentDir));
                }
            } catch (Exception e) {
                log.warn("getWatchMapperXmlFileDirPaths error resource={}", resource, e);
            }
        }
        return mapperXmlFileDirPaths;
    }
}
