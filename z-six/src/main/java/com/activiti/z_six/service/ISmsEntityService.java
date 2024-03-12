package com.activiti.z_six.service;

import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.entity.taskAssignee.OvTaskEntity;
import com.activiti.z_six.entity.taskAssignee.SmsEntity;
import com.activiti.z_six.tenant.model.FlowMessage;
import com.activiti.z_six.tenant.statusTrans.StatusEnum;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.task.model.Task;
import org.activiti.engine.history.HistoricProcessInstance;

import java.util.HashMap;

public interface ISmsEntityService {
    /**
     * 获取流程通知列表
     * @param toUser
     * @param pageNum
     * @param pagesize
     * @param status
     * @return
     */
    HashMap<String,Object> smsList(String toUser,Integer pageNum,Integer pagesize,String status);
    /**
     * 获取流程知会列表
     * @param toUser
     * @param pageNum
     * @param pagesize
     * @param status
     * @return
     */
    HashMap<String,Object> ccSmsList(String toUser,Integer pageNum,Integer pagesize,String status);
    /**
     * 获取一条消息信息
     * @param id
     * @return
     */
    public SmsEntity smsEntity(String id);

    /**
     * 发送流程审核消息
     *
     * @param toUsers
     * @param sender
     * @param processInstance
     * @param processTaskParams
     * @param ovTaskEntity
     * @param task
     * @param historicProcessInstance
     * @param endTask
     * @return
     */
    String sendFlowMsg(String toUsers, String sender, ProcessInstance processInstance,
                       ProcessTaskParams processTaskParams, OvTaskEntity ovTaskEntity, Task task, HistoricProcessInstance historicProcessInstance, Boolean endTask);

    void storeTenantStatusMessage(StatusEnum statusEnum, FlowMessage flowMessage, String tenant);

    /**
     * 发送抄送消息
     * @param processTaskParams
     * @return
     */
    String sendCopyForMsg(ProcessTaskParams processTaskParams);
    /**
     * 增加一条消息
     * @param smsEntity
     * @return
     */
    public String addSms(SmsEntity smsEntity);

    /**
     * 已读
     * @param id
     * @return
     */
    String updateState(String id);

    /**
     * 删除
     * @param id
     * @return
     */
    String deleteSms(String id);
}
