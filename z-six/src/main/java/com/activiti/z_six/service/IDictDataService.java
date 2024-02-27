package com.activiti.z_six.service;

import com.activiti.z_six.dto.controllerParams.DictDataParams;
import com.activiti.z_six.entity.dictData.DictDataEntity;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IDictDataService {
    /**
     * 查询字典列表
     * @param dictDataParams
     * @return
     */
    HashMap<String,Object> getDicts(DictDataParams dictDataParams);

    /**
     * 根据字典编号，获取字典详情
     * @param dictData
     * @return
     */
    List<Map<String,Object>> getDictAndData(DictDataEntity dictData);

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
    int deleteDict(List<DictDataEntity> dictDataEntities);
}
