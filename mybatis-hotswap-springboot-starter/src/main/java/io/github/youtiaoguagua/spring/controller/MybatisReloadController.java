package io.github.youtiaoguagua.spring.controller;

import io.github.youtiaoguagua.spring.core.MybatisMapperXmlFileReloadService;
import io.github.youtiaoguagua.spring.core.MybatisMapperXmlLoadService;
import io.github.youtiaoguagua.spring.entity.LoginReq;
import io.github.youtiaoguagua.spring.entity.LoginResp;
import io.github.youtiaoguagua.spring.entity.MapperXmlEntity;
import io.github.youtiaoguagua.spring.entity.Response;
import io.github.youtiaoguagua.spring.mybatis.MybatisMapperReloadProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author 王祥飞
 * @time 2021/12/13 5:26 PM
 */
@RestController
@RequestMapping("/api/wang/mybatis")
@Slf4j
public class MybatisReloadController {

    @Autowired
    private MybatisMapperXmlLoadService mybatisMapperXmlLoadService;

    @Autowired
    private MybatisMapperXmlFileReloadService mybatisMapperXmlFileReloadService;

    @Autowired
    private MybatisMapperReloadProperties properties;

    private final String cao = "/hhh";

    @GetMapping(value = {cao})
    public String getTest(){
        return "sdafas";
    }

    @PostMapping("/login")
    public Response<LoginResp> login(@RequestBody LoginReq loginReq) {
        if (!Objects.equals(loginReq.getUsername(), properties.getUsername()) || !Objects.equals(loginReq.getPassword(), properties.getPassword())) {
            return Response.fail("用户名或密码错误！");
        }
        LoginResp loginResp = new LoginResp();
        loginResp.setToken(properties.getToken());
        loginResp.setName(properties.getUsername());
        loginResp.setAvatar("https://ae03.alicdn.com/kf/H4c2aa642bcfc40d99bc5bd31ecc799c8J.png");
        return Response.ok(loginResp);
    }

    @GetMapping("/mapper")
    public Response<Collection<MapperXmlEntity>> getMappers() {
        Collection<MapperXmlEntity> mapperXmlEntities = mybatisMapperXmlLoadService.getMapperXmlEntity().values();
        ArrayList<MapperXmlEntity> result = new ArrayList<>();
        mapperXmlEntities.forEach(mapperXmlEntity -> {
            MapperXmlEntity build = MapperXmlEntity.builder().name(mapperXmlEntity.getName()).path(mapperXmlEntity.getPath()).build();
            result.add(build);
        });
        return Response.ok(result);
    }

    @GetMapping("/mapper/{mapperName}")
    public Response<MapperXmlEntity> getMapperXmlDetail(@PathVariable String mapperName) {
        String path = mybatisMapperXmlLoadService.getMapperXmlPath().get(mapperName);
        if (Objects.isNull(path)) {
            return Response.fail("没有找到对应的mapper！");
        }
        MapperXmlEntity entity = mybatisMapperXmlLoadService.getMapperXmlEntity().get(path);
        if (Objects.isNull(entity) || Objects.isNull(entity.getXml())) {
            return Response.fail("没有找到对应的xml文件！");
        }
        MapperXmlEntity mapperXmlEntity = MapperXmlEntity.builder().name(mapperName).path(path).xml(entity.getXml()).build();
        return Response.ok(mapperXmlEntity);
    }

    @PutMapping("/mapper")
    public Response<Boolean> updateMapperXml(@RequestBody MapperXmlEntity mapperXmlEntity) {
        if (!mapperXmlEntity.check()) {
            return Response.fail("参数不合法！");
        }
        MapperXmlEntity entity = mybatisMapperXmlLoadService.getMapperXmlEntity().get(mapperXmlEntity.getPath());
        if (Objects.isNull(entity)) {
            return Response.fail("没有找到对应的xml文件！");
        }
        boolean result = mybatisMapperXmlFileReloadService.reloadAllSqlSessionFactoryMapper(mapperXmlEntity.getXml(), mapperXmlEntity.getPath());

        if (result) {
            // 更新mybatis文件
            entity.setXml(mapperXmlEntity.getXml());
        }
        return Response.ok(result);
    }


}
