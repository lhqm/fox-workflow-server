package com.activiti.z_six.tenant.service;

import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface ProcessService {
    Map<String, Object> getFormJson(String processKey, String taskId);

    String submitForm(String processKey, String dataJson,String taskId) throws Exception;

    SendActionDto startProcess(ProcessTaskParams processTaskParams);

    ResultRes getProcessDefinition(String processInstanceId);

    ResultRes getProcessFormData(JSONObject jsonObject);
}
