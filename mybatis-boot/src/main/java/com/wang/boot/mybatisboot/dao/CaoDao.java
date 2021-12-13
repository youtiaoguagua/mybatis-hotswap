package com.wang.boot.mybatisboot.dao;

import com.wang.boot.mybatisboot.entity.Cao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Cao)表数据库访问层
 *
 * @author makejava
 * @since 2021-12-13 20:30:47
 */
@Mapper
public interface CaoDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Cao queryById(Integer id);

    List<Cao> queryLimit();

    /**
     * 统计总行数
     *
     * @param cao 查询条件
     * @return 总行数
     */
    long count(Cao cao);

    /**
     * 新增数据
     *
     * @param cao 实例对象
     * @return 影响行数
     */
    int insert(Cao cao);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Cao> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Cao> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Cao> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Cao> entities);

    /**
     * 修改数据
     *
     * @param cao 实例对象
     * @return 影响行数
     */
    int update(Cao cao);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

