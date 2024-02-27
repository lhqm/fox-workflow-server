package com.activiti.z_six.strategy.dict;

import com.activiti.z_six.entity.dictData.DictDataEntity;
import com.activiti.z_six.mapper.SqlMapper;
import com.activiti.z_six.util.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SqlStrategy implements DictStrategy{
    @Autowired
    private SqlMapper sqlMapper;
    @Override
    public List<Map<String,Object>> getDictData(DictDataEntity dictDataEntity){
        List<Map<String,Object>> hashMapList=new ArrayList<>();
        String doc=dictDataEntity.getDoc();
        if(!SystemConfig.IsNullOrEmpty(doc)){
            hashMapList=sqlMapper.list(doc,null);
        }
        return hashMapList;
    }
    @Override
    public String getType(){
        return "1";
    }
}
