package com.activiti.z_six.tenant.service;

import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;

import java.util.Map;

public interface ProcessService {
    Map<String, Object> getFormJson(String processKey, String taskId);

    String submitForm(String processKey, String dataJson,String taskId) throws Exception;

    SendActionDto startProcess(ProcessTaskParams processTaskParams);
}
