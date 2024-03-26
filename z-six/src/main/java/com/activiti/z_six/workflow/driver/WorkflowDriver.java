package com.activiti.z_six.workflow.driver;

import com.activiti.z_six.entity.tenant.FlowProcess;
import com.activiti.z_six.tenant.model.api.FlowMessage;
import com.activiti.z_six.tenant.model.api.ProcessStart;
import com.activiti.z_six.workflow.model.FormData;
import com.activiti.z_six.workflow.model.SubmitData;

import java.util.List;

/**
 * @description: 工作流中台驱动接口
 */
public interface WorkflowDriver {
    FormData getFormData(String processKey, String taskId);
    String submitForm(SubmitData formData);
    List<FlowProcess> getFlowElementsByProcessKey(String processKey);
    List<FlowMessage> getMessages();
    Integer confirmMessage(String ackCode);
    String startProcess(ProcessStart processStart, String user);

}
