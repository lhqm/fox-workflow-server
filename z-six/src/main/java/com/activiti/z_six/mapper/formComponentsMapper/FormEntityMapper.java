package com.activiti.z_six.mapper.formComponentsMapper;

import com.activiti.z_six.entity.formComponents.CreateTable;
import com.activiti.z_six.entity.formComponents.FormEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FormEntityMapper {
    /**
    获取表单模版信息
     */
    FormEntity formEntity(String id);
    /**
    获取表单模版列表
     */
    List<FormEntity> formList(@Param("name") String name,@Param("formSort") String formSort);

    /**
     * 表单分页列表
     * @param page
     * @param pagesize
     * @return
     */
    List<FormEntity> formListPage(@Param("name") String name,@Param("formSort") String formSort,
                                  @Param("page") Integer page, @Param("pagesize")Integer pagesize);

    /**
     * 获取表单树
     * @return
     */
    List<FormEntity> getFormTree();
    List<FormEntity> getFormTreeByAdd();
    /**
    增加
     */
    int addForm(FormEntity formEntity);
    /*
    修改
     */
    int updateForm(FormEntity formEntity);

    /**
     * 删除表单
     * @param id
     * @return
     */
    int deleteForm(String id);

    /**
     * 创建存储表
     * @param createTable
     * @return
     */
    int createFormTable(CreateTable createTable);

    /**
     * 修改表字段
     * @param createTable
     * @return
     */
    int AlterTableAddField(CreateTable createTable);

    /**
     * 是否存在表
     * @param tableName
     * @param dataSourceName
     * @return
     */
    int isTableExist(@Param("tableName") String tableName,
                     @Param("dataSourceName")String dataSourceName);

    /**
     * 获取表中的列
     * @param tableName
     * @return
     */
    List<String> findFieldByTableName(String tableName);
    List<Map<String,Object>> findFieldByTable(@Param("tableName") String tableName,
                                              @Param("table_schema") String table_schema);
}
