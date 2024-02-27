package com.activiti.z_six.strategy.manager;

import com.activiti.z_six.entity.taskAssignee.GenerWork;
import com.activiti.z_six.mapper.SqlMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.GenerWorkMapper;
import com.activiti.z_six.util.HttpsUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoEventManager {
    @Autowired
    private final GenerWorkMapper generWorkMapper;
    @Autowired
    private final SqlMapper sqlMapper;

    /**
     * 获取执行参数
     * @param businessKey
     * @param proc_inst_id
     * @return
     */
    public HashMap<String,Object> EvenParams(String businessKey,String proc_inst_id){
        HashMap<String, Object> hashMap = new HashMap<>();
        GenerWork generWork=generWorkMapper.generWork(businessKey);
        //业务数据主键
        hashMap.put("businessKey", businessKey);
        //流程运行实例id
        hashMap.put("proce_inst_id", proc_inst_id);
        //流程key
        hashMap.put("processKey", generWork.getProcesskey());
        //业务表数据
        hashMap.put("others", generWork.getData_json());
        return hashMap;
    }

    /**
     * 根据执行方式执行事件
     * @param doWay
     * @param eventDoc
     * @param params
     * @return
     */
    public Boolean DoEventByWay(String doWay,String eventDoc,Map<String,Object> params){
        try{
            //如果是执行sql语句
            if(doWay.equals("1")){
                //执行查询
                if (eventDoc.toLowerCase().contains("select"))
                    sqlMapper.list(eventDoc, params);
                //执行更新
                else if (eventDoc.toLowerCase().contains("update"))
                    sqlMapper.update(eventDoc, params);
                //执行新增
                else if (eventDoc.toLowerCase().contains("insert"))
                    sqlMapper.insert(eventDoc, params);
                //执行删除
                else if (eventDoc.toLowerCase().contains("delete"))
                    sqlMapper.delete(eventDoc, params);
                return true;
            }
            //如果是执行webapi
            if (doWay.equals("2")){
                //增加header参数
                java.util.Map<String, String> headerMap = new Hashtable<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("charset", "utf-8");

                String jsonData = HttpsUtils.post(eventDoc, headerMap, params.toString());
                JSONObject json=JSONObject.parseObject(jsonData);
                if(json.getString("code").equals("0")){
                    return true;
                }
                else {
                    return false;
                }
            }
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
}
