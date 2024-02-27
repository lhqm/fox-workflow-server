package com.activiti.z_six.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SqlMapper {

    @Select("${sql}")
    Map<String, Object> single(@Param("sql") String sql, @Param("param") Map<String, Object> param);

    @Select("${sql}")
    Long count(@Param("sql") String sql, @Param("param") Map<String, Object> param);

    @Select("${sql}")
    List<Map<String, Object>> list(@Param("sql") String sql, @Param("param") Map<String, Object> param);

    @Insert("${sql}")
    Integer insert(@Param("sql") String sql, @Param("param") Map<String, Object> param);

    @Update("${sql}")
    Integer update(@Param("sql") String sql, @Param("param") Map<String, Object> param);

    @Delete("${sql}")
    Integer delete(@Param("sql") String sql, @Param("param") Map<String, Object> param);

    @Select("$sql")
    List<String> descTable(@Param("sql") String sql, @Param("param") Map<String, Object> param);
}