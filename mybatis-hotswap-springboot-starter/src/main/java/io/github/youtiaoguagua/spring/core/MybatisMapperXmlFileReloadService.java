package io.github.youtiaoguagua.spring.core;

import cn.hutool.core.io.file.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 王祥飞
 * @time 2021/12/13 2:51 PM
 */
@Slf4j
public class MybatisMapperXmlFileReloadService {

    private List<SqlSessionFactory> sqlSessionFactoryList;


    public MybatisMapperXmlFileReloadService(List<SqlSessionFactory> sqlSessionFactoryList) {
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    /**
     * 重新 加载mapper xml
     *
     * @param mapperFilePath
     * @return
     */
    public boolean reloadAllSqlSessionFactoryMapper(String mapperFilePath) {
        if (CollectionUtils.isEmpty(sqlSessionFactoryList)) {
            log.warn("not find SqlSessionFactory bean");
            return false;
        }

        Path path = Paths.get(mapperFilePath);
        if (!Files.exists(path)) {
            log.warn("mybatis reload mapper xml not exist ={}", mapperFilePath);
            return false;
        }

        AtomicBoolean result = new AtomicBoolean(true);
        File file = path.toFile();
        FileReader fileReader = new FileReader(file);
        String xml = fileReader.readString();
        // 删除mapper 缓存 重新加载
        sqlSessionFactoryList.parallelStream().forEach(sqlSessionFactory -> {
            Configuration configuration = sqlSessionFactory.getConfiguration();

            if (!this.removeMapperCacheAndReloadNewMapperFile(xml, configuration)) {
                log.warn("reload new mapper file fail path={}", path.toString());
                result.set(false);
            } else {
                log.info("reload new mapper file success path={}", path.toString());
            }
        });
        return result.get();
    }

    public boolean reloadAllSqlSessionFactoryMapper(String xml, String path) {
        AtomicBoolean result = new AtomicBoolean(true);

        // 删除mapper 缓存 重新加载
        sqlSessionFactoryList.parallelStream().forEach(sqlSessionFactory -> {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            if (!this.removeMapperCacheAndReloadNewMapperFile(xml, configuration)) {
                log.warn("reload new mapper file fail path={}", path.toString());
                result.set(false);
            } else {
                log.info("reload new mapper file success path={}", path.toString());
            }
        });
        return result.get();
    }


    private Object readField(Object target, String name) {
        Field field = ReflectionUtils.findField(target.getClass(), name);
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, target);
    }

    /**
     * 删除老的mapper 缓存 加载新的mapper 文件
     *
     * @param configuration
     * @return
     */
    private boolean removeMapperCacheAndReloadNewMapperFile(String xml, Configuration configuration) {
        try (InputStream fileInputStream = new ByteArrayInputStream(xml.getBytes())) {
            XPathParser context = new XPathParser(fileInputStream, true, configuration.getVariables(), new XMLMapperEntityResolver());
            XNode contextNode = context.evalNode("/mapper");
            if (null == contextNode) {
                return false;
            }
            String namespace = contextNode.getStringAttribute("namespace");
            if (namespace == null || namespace.isEmpty()) {
                throw new BuilderException("Mapper's namespace cannot be empty");
            }

            this.removeOldMapperFileConfigCache(configuration, contextNode, namespace);
            this.addNewMapperFile(configuration, xml, namespace);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 删除老的mapper 相关的配置文件
     *
     * @param configuration
     * @param mapper
     * @param namespace
     * @see XMLMapperBuilder#configurationElement
     */
    private void removeOldMapperFileConfigCache(Configuration configuration, XNode mapper, String namespace) {
        String xmlResource = parserNameSpace(namespace);
        ((Set<?>) this.readField(configuration, "loadedResources")).remove(xmlResource);

        Map<?, ?> parameterMaps = (Map<?, ?>) this.readField(configuration, "parameterMaps");
        Map<?, ?> resultMaps = (Map<?, ?>) this.readField(configuration, "resultMaps");
        Map<?, ?> sqlFragments = (Map<?, ?>) this.readField(configuration, "sqlFragments");
        Map<?, ?> mappedStatements = (Map<?, ?>) this.readField(configuration, "mappedStatements");

        for (XNode node : mapper.evalNodes("parameterMap")) {
            String parameterMapId = this.resolveId(namespace, node.getStringAttribute("id"));
            parameterMaps.remove(parameterMapId);
        }
        for (XNode node : mapper.evalNodes("resultMap")) {
            String resultMapId = this.resolveId(namespace, node.getStringAttribute("id"));
            resultMaps.remove(resultMapId);
        }
        for (XNode node : mapper.evalNodes("sql")) {
            String sqlId = this.resolveId(namespace, node.getStringAttribute("id"));
            sqlFragments.remove(sqlId);
        }
        for (XNode node : mapper.evalNodes("select|insert|update|delete")) {
            String statementId = this.resolveId(namespace, node.getStringAttribute("id"));
            mappedStatements.remove(statementId);
        }
    }

    /**
     * 加载新的mapper 文件
     *
     * @param configuration
     * @param namespace
     */
    private void addNewMapperFile(Configuration configuration, String xml, String namespace) throws IOException {
        try (InputStream fileInputStream = new ByteArrayInputStream(xml.getBytes());) {
            String xmlResource = parserNameSpace(namespace);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(fileInputStream, configuration,
                    xmlResource,
                    configuration.getSqlFragments());
            xmlMapperBuilder.parse();
        }
    }

    private String parserNameSpace(String namespace) {
        String xmlResource = "namespace:" + namespace;
        return xmlResource;
    }


    private String resolveId(String namespace, String id) {
        return namespace + "." + id;
    }
}
