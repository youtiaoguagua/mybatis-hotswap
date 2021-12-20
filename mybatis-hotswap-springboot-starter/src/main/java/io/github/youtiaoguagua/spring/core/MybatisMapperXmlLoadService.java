package io.github.youtiaoguagua.spring.core;

import io.github.youtiaoguagua.spring.entity.MapperXmlEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
    private HashMap<String, String> mapperXmlPath = new HashMap<>();

    @Getter
    private HashMap<String, MapperXmlEntity> mapperXmlEntity = new HashMap<>();

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
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        this.mappingResources = Stream.of(Optional.ofNullable(locations).orElse(new String[0]))
                .flatMap(location -> {
                    try {
                        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(location);
                        return Stream.of(resources);
                    } catch (IOException e) {
                        log.warn("scan mapper xml error location={}", location, e);
                    }
                    return null;
                }).filter(Objects::nonNull).peek(resource -> {
                    try {
                        String xml = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
                        String path = resource.getURL().getPath();
                        String filename = resource.getFilename();
                        MapperXmlEntity mapperXmlEntity = MapperXmlEntity.builder().name(filename).path(path).xml(xml).build();
                        this.mapperXmlEntity.put(path, mapperXmlEntity);
                        mapperXmlPath.put(filename, path);
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
