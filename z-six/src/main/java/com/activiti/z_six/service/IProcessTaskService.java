package com.activiti.z_six.service;

import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.dto.controllerParams.CCParams;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.dto.controllerParams.TaskManageParams;
import com.activiti.z_six.entity.taskAssignee.*;
import org.activiti.bpmn.model.UserTask;

import java.util.HashMap;
import java.util.List;

public interface IProcessTaskService {
    /**
    启动流程
    processDefinitionKey:流程实例key
    instanceName：流程名称
    variables：自定义参数
     */
    SendActionDto startProcess(ProcessTaskParams processTaskParams);
    /**
    流程审核
    taskId：任务id
    variables：自定义参数
     */
    SendActionDto sendWork(ProcessTaskParams processTaskParams);
    /**
    触发等待任务
     */
    String sendReceiceTask(String processInstanceId, String taskKey, String executionId);
    /**
    退回
     */
    OvTaskEntity returnWork(ReturnWorkEntity returnWorkEntity);
    /**
    移交
     */
    OvTaskEntity transfer(TaskManageParams params);

    /**
     * 批量移交
     * @param params
     * @return
     */
    String transferBatch(TaskManageParams params);
    /**
    加签
     */
    OvTaskEntity countersign(TaskManageParams params);
    /**
    拒绝
     */
    void refuse(TaskManageParams params);
    /**
    强制结束
     */
    void setEndTask(TaskManageParams params);

    /**
     * 批量结束
     * @param params
     */
    String setEndTaskBatch(TaskManageParams params);
    /**
     * 获取审核按钮
     * @param taskid
     * @return
     */
    FlowElementAttrs getElementButton(String taskid);

    /**
     * 删除流程实例
     * @param params
     */
    void deleteProcInst(TaskManageParams params);
    /**
    获取下一步节点信息
     */
    List<UserTask> getNextTaskInfo(String procInstId, String taskid, HashMap<String,Object> variables);
    /**
    获取下一步任务处理人
     */
    HashMap<String,Object> getNextUserInfo(String procInstId,String taskid,HashMap<String,Object> variables);

    /**
     * 获取待办任务
     * @param pageNum
     * @param pagesize
     * @param flowName
     * @return
     */
    HashMap<String,Object> getTodoList(Integer pageNum,Integer pagesize,String flowName);
    /**
    获取待办任务列表
    flowSort:流程类别
    proceId:流程实例ID，类似流程编号
     */
    HashMap<String,Object> getTodoTask(String flowSort, String proceId, Integer page, Integer pagesize);
    /**
    获取已审核任务列表
    flowSort:流程类别
    proceId:流程实例ID，类似流程编号
     */
    HashMap<String, Object> getHistoricTaskInstance(String flowName, Integer pageNum
            , Integer pagesize);

    /**
     * 获取绑定的表单
     * @param processKey
     * @param taskid
     * @return
     */
    HashMap<String, Object> getHisFormJson(String processKey,String taskid);
    /**
    设置标题
     */
    public String setTitle(String proce_inst_id,String id,String processKey);
    /**
     * 获取手动选择接收人列表
     * @param bmsType
     * @param task_def_id
     * @param page
     * @param pagesize
     * @return
     */
    HashMap<String,Object>  getSelectNextUsers(String bmsType, String task_def_id,
                                        Integer page, Integer pagesize);

    /**
     * 设置下一步接收人（手动选择有效）
     * @param manuallySelectEntityList
     * @return
     */
    String setNextToUsers(List<ManuallySelectEntity> manuallySelectEntityList);
}
