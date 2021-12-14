package com.wang.spring.controller;

import com.wang.spring.core.MybatisMapperXmlFileReloadService;
import com.wang.spring.core.MybatisMapperXmlLoadService;
import com.wang.spring.entity.MapperXmlEntity;
import com.wang.spring.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author 王祥飞
 * @time 2021/12/13 5:26 PM
 */
@RestController
@RequestMapping("/api/wang/mybatis")
@Slf4j
public class MybatisController {

    @Autowired
    private MybatisMapperXmlLoadService mybatisMapperXmlLoadService;

    @Autowired
    private MybatisMapperXmlFileReloadService mybatisMapperXmlFileReloadService;

    @GetMapping("/mapper")
    public Response<HashMap<String, String>> getMappers() {
        HashMap<String, String> mapperXmlPath = mybatisMapperXmlLoadService.getMapperXmlPath();
        return Response.ok(mapperXmlPath);
    }

    @GetMapping("/mapper/{mapperName}")
    public Response<MapperXmlEntity> getMapperXmlDetail(@PathVariable String mapperName) {
        String path = mybatisMapperXmlLoadService.getMapperXmlPath().get(mapperName);
        if (Objects.isNull(path)) {
            return Response.fail("没有找到对应的mapper！");
        }
        String xml = mybatisMapperXmlLoadService.getMapperXmlFileStream().get(path);
        if (Objects.isNull(xml)) {
            return Response.fail("没有找到对应的xml文件！");
        }
        MapperXmlEntity mapperXmlEntity = MapperXmlEntity.builder().name(mapperName).path(path).xml(xml).build();
        return Response.ok(mapperXmlEntity);
    }

    @PutMapping("/mapper")
    public Response<Boolean> updateMapperXml(@RequestBody MapperXmlEntity mapperXmlEntity) {
        if (!mapperXmlEntity.check()) {
            return Response.fail("参数不合法！");
        }
        boolean result = false;
        try (InputStream xmlStream = new ByteArrayInputStream(mapperXmlEntity.getXml().getBytes())) {
            result = mybatisMapperXmlFileReloadService.reloadAllSqlSessionFactoryMapper(xmlStream, mapperXmlEntity.getPath());
        } catch (IOException e) {
            log.error("更新mapper失败！", e);
        }
        if (result) {
            // 更新mybatis文件
            mybatisMapperXmlLoadService.getMapperXmlFileStream().put(mapperXmlEntity.getPath(), mapperXmlEntity.getXml());
        }
        return Response.ok(result);
    }


}
