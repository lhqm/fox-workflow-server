package com.activiti.z_six.service;

import com.activiti.z_six.dto.controllerParams.ProcessDefinitionParams;
import com.activiti.z_six.dto.controllerParams.TaskDefinitionParams;
import com.activiti.z_six.entity.process.FlowSort;

import java.util.HashMap;
import java.util.List;

public interface IProcessDefinitionService {
    /**
     * 获取流程部署列表
     * @param page
     * @param pagesize
     * @param sortid
     * @param name
     * @return
     */
    HashMap<String ,Object> getDeployment(Integer page, Integer pagesize, String sortid, String name);
    /**
     * 发布流程
     *
     * @param BPMNXml
     * @param tenantId
     * @return
     */
    String deployWithBPMNJS(String BPMNXml, String tenantId);

    /**
     * 流程属性
     * @param process_key
     * @return
     */
    ProcessDefinitionParams processProperties(String process_key);

    /**
     * 保存流程属性
     * @return
     */
    String saveProcessProperties(ProcessDefinitionParams processDefinitionParams);

    /**
     * 节点属性
     * @param task_def_key
     * @return
     */
    TaskDefinitionParams userTaskProperties(String task_def_key);

    /**
     * 更新节点属性
     * @param taskDefinitionParams
     * @return
     */
    String saveUserTaskProperties(TaskDefinitionParams taskDefinitionParams);
    /**
     * 获取流程分类分页
     * @param page
     * @param pagesize
     * @return
     */
    HashMap<String ,Object> getFlowSort(Integer page, Integer pagesize);
    /**
     * 获取全部流程分类
     * @return
     */
    List<HashMap<String, Object>> getFlowSortAll();
    /**
     * 获取全部流程分类树
     * @return
     */
    List<FlowSort> getFlowSortTree();
    /**
     * 获取流程发起页面数据
     * @return
     */
    HashMap getSatrtPageData();
    /**
     * 新增流程类别
     * @param name
     * @param parentId
     * @return
     */
    String addFlowSort(String name, Long parentId);
    /**
     * 流程类别保存修改
     * @param id
     * @param name
     * @param parentId
     * @return
     */
    String saveFlowSort(Long id, String name, Long parentId);

    /**
     * 删除流程定义
     * @param deploymentId
     * @return
     */
    String deleteDefinition(String deploymentId);

    /**
     * 删除流程分类
     * @param id
     * @param name
     * @param parentId
     * @return
     */
    String deleteFlowSort(Long id, String name, Long parentId);
}
