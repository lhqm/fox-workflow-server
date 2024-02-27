package com.activiti.z_six.mapper.formComponentsMapper;

import com.activiti.z_six.entity.formComponents.FormSortEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FormSortEntityMapper {
    /**
     * 获取表单分类列表
     * @return
     */
    List<FormSortEntity> formSortList();

    /**
     * 增加表单分类
     * @param formSortEntity
     * @return
     */
    int addFormSort(FormSortEntity formSortEntity);

    /**
     * 更新表单分类信息
     * @param formSortEntity
     * @return
     */
    int updateFormSort(FormSortEntity formSortEntity);

    /***
     * 删除表单分类
     * @param id
     * @return
     */
    int deleteFormSort(String id);
    /*
    获取单条详细信息
     */
    FormSortEntity formSort(String id);
}
