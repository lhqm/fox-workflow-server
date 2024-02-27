package com.activiti.z_six.strategy.dict;

import com.activiti.z_six.entity.dictData.DictDataEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public interface DictStrategy {
    /**
     * 获取字典数据
     * @param dictDataEntity
     * @return
     */
    List<Map<String,Object>> getDictData(DictDataEntity dictDataEntity);

    String getType();
}
