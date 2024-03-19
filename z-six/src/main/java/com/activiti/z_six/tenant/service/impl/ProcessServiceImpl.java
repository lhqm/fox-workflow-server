package com.activiti.z_six.tenant.service.impl;

import com.activiti.z_six.SecurityUtil;
import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.dto.controllerParams.FormDataValue;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.entity.tenant.FlowProcess;
import com.activiti.z_six.service.IFormValueService;
import com.activiti.z_six.service.IProcessTaskService;
import com.activiti.z_six.tenant.service.ProcessService;
import com.activiti.z_six.util.ResultRes;
import com.activiti.z_six.util.flow.FlowElementUtil;
import com.activiti.z_six.util.req.RequestUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ProcessServiceImpl implements ProcessService {
    @Autowired
    private IProcessTaskService processTaskService;
    @Autowired
    private IFormValueService formValueService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessRuntime processRuntime;
    @Override
    public Map<String, Object> getFormJson(String processKey, String taskId) {
        String userName = RequestUtils.getUserName();
        return processTaskService.getHisFormJson(processKey, taskId,userName);
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
        String starter = RequestUtils.getUserName();
        securityUtil.loginSafely(starter);
        return processTaskService.startProcess(processTaskParams,starter);
    }

    @Override
    public ResultRes getProcessDefinition(String processInstanceId) {
        return ResultRes.success(getFlowElementsByProcessDefinition(processInstanceId));
    }


    /**
     * 获取到对应流程定义ID的整体流程信息
     * @param definitionId 流程定义信息ID
     * @return 流程定义信息
     */

    public List<FlowProcess> getFlowElementsByProcessDefinition(String definitionId){
//        获取到流程的模型实例
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
//        在模型的流程表里选择第一个流程作为流程对象定义（一般也只有一个）
        Process process = bpmnModel.getProcesses().get(0);
//        返回数据，这里开始对流程信息进行解构，获取到所有用户任务。因为用户任务才能参与审核，审核才会唤醒流转，流转才会改变状态
        Collection<FlowElement> flowElements = process.getFlowElements();
//        获取到流程路径定义
        List<FlowProcess> flowDefinitionMap = FlowElementUtil.getFlowDefinitionMap(flowElements);
        for (FlowProcess flowProcess : flowDefinitionMap) {
            System.out.println(JSONArray.toJSONString(flowProcess));
        }
        return flowDefinitionMap;
    }

    /**
     * 通过流程实例ID去获取流程定义
     * @param instanceId 流程实例ID
     * @return 流程定义信息
     */

    public List<FlowProcess> getFlowElementsByProcessInstance(String instanceId){
        ProcessInstance processInstance = processRuntime.processInstance(instanceId);
        return getFlowElementsByProcessDefinition(processInstance.getProcessDefinitionId());
    }


}
