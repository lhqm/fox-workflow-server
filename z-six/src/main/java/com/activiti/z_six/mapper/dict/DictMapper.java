package com.activiti.z_six.mapper.dict;

import com.activiti.z_six.entity.dictData.DictDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DictMapper {
    /**
     * 查询字典列表
     * @param name
     * @param status
     * @return
     */
    List<DictDataEntity> getDicts(@Param("name") String name, @Param("status") String status);

    /**
     * 字典查询分页
     * @param name
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    List<DictDataEntity> getDictsPage(@Param("name") String name, @Param("status") String status,
                                      @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    /**
     * 根据字典编号，获取字典详情
     * @param no
     * @return
     */
    DictDataEntity dictDataEntity(String no);

    /**
     * 增加字典
     * @param dictData
     * @return
     */
    int addDict(DictDataEntity dictData);

    /**
     * 更新字典
     * @param dictData
     * @return
     */
    int updateDict(DictDataEntity dictData);

    /**
     * 删除字典
     * @param dictDataEntities
     * @return
     */
    int deleteDict(@Param("dictDataEntities") List<DictDataEntity> dictDataEntities);
}
