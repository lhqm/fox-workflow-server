package com.activiti.z_six.strategy.dict;

import com.activiti.z_six.entity.dictData.DictDataEntity;
import com.activiti.z_six.util.SystemConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ParamsStrategy implements DictStrategy{
    @Override
    public List<Map<String,Object>> getDictData(DictDataEntity dictDataEntity){
        List<Map<String,Object>> hashMapList=new ArrayList<>();
        String doc=dictDataEntity.getDoc();
        String[] params=doc.split("@");
        for(String str:params){
            if(SystemConfig.IsNullOrEmpty(str))
                continue;
            String[] keyAndValue=str.split("=");
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("label",keyAndValue[1]);
            hashMap.put("value",keyAndValue[0]);
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }
    @Override
    public String getType(){
        return "0";
    }
}
