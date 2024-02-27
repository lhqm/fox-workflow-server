package com.activiti.z_six.strategy.dict;

import com.activiti.z_six.entity.dictData.DictDataEntity;
import com.activiti.z_six.util.HttpsUtils;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ApiStrategy implements DictStrategy{
    @Override
    public List<Map<String,Object>> getDictData(DictDataEntity dictDataEntity){
        List<Map<String,Object>> hashMapList=new ArrayList<>();
        String doc=dictDataEntity.getDoc();
        if(!SystemConfig.IsNullOrEmpty(doc)){
            //增加header参数
            java.util.Map<String, String> headerMap = new Hashtable<String, String>();
            headerMap.put("Content-Type", "application/json");
            headerMap.put("charset", "utf-8");


            //请求参数
            HashMap<String,Object> objectHashMap=new HashMap<>();
            try {
                //请求返回信息json
                JSONObject json = null;
                String jsonData = HttpsUtils.get(doc, headerMap, objectHashMap.toString());
                json=JSONObject.parseObject(jsonData);
                JSONArray jsonArray=JSONArray.parseArray(json.get("result").toString());

                for(int i=0;i<jsonArray.size();i++){
                    HashMap<String,Object> hashMap=new HashMap<>();
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    hashMap.put("label",jsonObject.get("name").toString());
                    hashMap.put("value",jsonObject.get("no").toString());
                    hashMapList.add(hashMap);
                }
            }
            catch (Exception ex){
                return hashMapList;
            }
        }
        return hashMapList;
    }
    @Override
    public String getType(){
        return "2";
    }
}
