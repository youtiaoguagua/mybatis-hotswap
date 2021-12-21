# Mybatis XML热加载

[![Maven Package](https://github.com/youtiaoguagua/mybatis-hotswap/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/youtiaoguagua/mybatis-hotswap/actions/workflows/maven-publish.yml)
![GitHub](https://img.shields.io/github/license/youtiaoguagua/mybatis-hotswap)
![version](https://img.shields.io/maven-central/v/io.github.youtiaoguagua/mybatis-hotswap-springboot-starter)

本项目为了方便在开发过程中修改mybatis xml文件，分为本地修改和线上修改两部分。

* 本地开发时只需要修改完xml文件，recompile之后插件会自动检测到文件变更，重新加载xml。

* 线上只需打开web界面即可在页面上直接编辑xml。

## 1、参考项目

  * [mybatis-mapper-reload-spring-boot-start](https://github.com/WangJi92/mybatis-mapper-reload-spring-boot-start)

## 2、原理
mybatis在springboot中自动配置的方法会获取一个`SqlSessionFactory`，在设置bean时会调用`getObject`，这个方法将会调用`afterPropertiesSet`，这个方法将会构造一个`sqlSessionFactory`。

构造`sqlSessionFactory`时会循环创建`XMLMapperBuilder`，这个类会解析xml文件，并且将解析出来的sql语句和对应的方法绑定到`SqlSessionFactory`的`Configuration`中，`Configuration`中存储这xml的所有解析信息。

## 3、使用

```yaml
mybatis:
  mapper-locations: classpath*:/mapper/*.xml
  mapper:
    reload:
      # 是否开启重载
      enable: true
      # mybatis xml文件路径
      mapper-locations: classpath*:/mapper/*.xml
      # web见面请求路径
      url-prefix: web/mybatis/reload/
      # token加密密匙
      key: 123456
      # 登录用户名
      username: admin
      # 登录密码
      password: 123456
```

```xml
<dependency>
    <groupId>io.github.youtiaoguagua</groupId>
    <artifactId>mybatis-hotswap-springboot-starter</artifactId>
    <version>1.0.6-RELEASE</version>
</dependency>
```



## 4、预览

### 登录界面

![login](./image/login.png)

### XML列表

![list](./image/list.png)

### 编辑XML

![edit](./image/edit.png)


## Licence

This project is released under the [Apache License 2.0](https://github.com/youtiaoguagua/mybatis-hotswap/blob/master/LICENSE)