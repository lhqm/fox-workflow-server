package com.activiti.z_six.tenant.service.impl;

import com.activiti.z_six.SecurityUtil;
import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.dto.controllerParams.FormDataValue;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.service.IFormValueService;
import com.activiti.z_six.service.IProcessTaskService;
import com.activiti.z_six.tenant.service.ProcessService;
import com.activiti.z_six.util.req.RequestUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProcessServiceImpl implements ProcessService {
    @Autowired
    private IProcessTaskService processTaskService;
    @Autowired
    private IFormValueService formValueService;
    @Autowired
    private SecurityUtil securityUtil;
    @Override
    public Map<String, Object> getFormJson(String processKey, String taskId) {
        return processTaskService.getHisFormJson(processKey, taskId);
    }
    @Override
    public String submitForm(String processKey, String dataJson,String taskId) throws Exception {
        /*进行自动填充
        * dataJson的数据结构大致如下：<表单字段名称,表单字段对应值>
        * 而表单数据可以归结为两个描述字段：
        * config:表单主体定义，包括存储的物理表、对应的版本、是否开启动态表映射等
        * list:表单内各个字段的定义
        */
//        先获取到传过来的数据对应的表单定义这些
        Map<String, Object> formJson = getFormJson(processKey, taskId);
//        再拿到表单相关定义数据
        JSONObject mapJson = JSONObject.parseObject(formJson.get("mapJson").toString());
//        去mapJson里取到对应数据
        JSONObject dataMap = JSONObject.parseObject(dataJson);
        JSONArray fields = mapJson.getJSONArray("list");
//        循环进行自动填充
        for (int i = 0; i < fields.size(); i++) {
//            取到其字段进行填充
            JSONObject data = JSONObject.parseObject(fields.get(i).toString());
            if (dataMap.containsKey(data.getString("id"))){
//                向其中填充数据
                data.put("value",dataMap.get(data.getString("id")));
//                替换数据
                fields.set(i,data);
            }
        }
//        最后去加载数据
            return formValueService.saveFormValueByJson(new FormDataValue(
                    taskId,processKey,
                    formJson.containsKey("form_type")?formJson.get("form_type").toString():"0",
                    formJson.containsKey("form_url")?formJson.get("form_type").toString():"",
                    JSON.toJSONString(mapJson),
                    dataJson
                    ));
    }

    @Override
    public SendActionDto startProcess(ProcessTaskParams processTaskParams) {
        String starter = RequestUtils.getAllRequestHeaders().get("starter");
        if (starter == null){starter = "admin";}
        securityUtil.logInOnlyAct(starter);
        return processTaskService.startProcess(processTaskParams,starter);
    }


}
