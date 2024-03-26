package com.activiti.z_six.service.impl;

import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.entity.taskAssignee.GenerWork;
import com.activiti.z_six.entity.taskAssignee.OvTaskEntity;
import com.activiti.z_six.entity.taskAssignee.SmsEntity;
import com.activiti.z_six.mapper.taskAssigneeMapper.GenerWorkMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.OvTaskEntityMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.SmsEntityMapper;
import com.activiti.z_six.service.ISmsEntityService;
import com.activiti.z_six.tenant.service.WorkFlowMessageContext;
import com.activiti.z_six.tenant.model.api.FlowMessage;
import com.activiti.z_six.tenant.statusTrans.StatusEnum;
import com.activiti.z_six.util.SecurityUtils;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.task.model.Task;
import org.activiti.engine.history.HistoricProcessInstance;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ISmsEntityServiceImpl implements ISmsEntityService {
    @Autowired
    private SmsEntityMapper smsEntityMapper;
    @Autowired
    private GenerWorkMapper generWorkMapper;
    @Autowired
    private WorkFlowMessageContext workFlowMessageContext;
    @Autowired
    private OvTaskEntityMapper ovTaskEntityMapper;

    /**
     * 获取流程通知列表
     * 搞不懂这作者为啥只给查审核详情，
     * 我直接给查了所有的消息
     * 因为流程审核通过的消息都不显示了，就离谱
     * @param toUser
     * @param pageNum
     * @param pagesize
     * @param status
     * @return
     */
    @Override
    public HashMap<String,Object> smsList(String toUser,Integer pageNum,Integer pagesize,String status){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<SmsEntity> smsEntityList=smsEntityMapper.smsList(toUser,pageNum,pagesize,status);
        List<SmsEntity> smsEntities=smsEntityMapper.smsListNum(toUser,status);
        hashMap.put("list",smsEntityList);
        hashMap.put("total",smsEntities.size());
        return hashMap;
    }

    /**
     * 获取流程知会列表
     * @param toUser
     * @param pageNum
     * @param pagesize
     * @param status
     * @return
     */
    @Override
    public HashMap<String,Object> ccSmsList(String toUser,Integer pageNum,Integer pagesize,String status){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<SmsEntity> smsEntityList=smsEntityMapper.ccSmsList(toUser,pageNum,pagesize,status);
        List<SmsEntity> smsEntities=smsEntityMapper.ccSmsListNum(toUser,status);
        hashMap.put("list",smsEntityList);
        hashMap.put("total",smsEntities.size());
        return hashMap;
    }
    @Override
    public SmsEntity smsEntity(String id){
        SmsEntity smsEntity=smsEntityMapper.smsEntity(id);
        return smsEntity;
    }

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
    @Override
    public String sendFlowMsg(String toUsers, String sender, ProcessInstance processInstance
            , ProcessTaskParams processTaskParams, OvTaskEntity ovTaskEntity, Task task, HistoricProcessInstance historicProcessInstance, Boolean endTask){
        /**
         * notice:
         * 这一部分的代码写的有些不尽人意
         * 在有限分支里，将是否是终结点任务列举出来，这是正确的。但是将单人和多人分开是不对的。
         * 因为users字段里就算只有一个人，也可以封装成一个长度1的数组去解决，提升代码简洁性和可读性。
         * @TODO: 优化这部分的代码，把分支合并一下。
         *
         *
         * @release: 新增租户端推送支持。
         */
        FlowMessage flowMessage = FlowMessage
                .builder()
//                设置实例ID
                .processInstanceId(processInstance.getId())
                .businessKey(processInstance.getBusinessKey())
//                设置签发时间
                .processTime(
                        task.getCompletedDate()==null?
                                (task.getClaimedDate()==null?System.currentTimeMillis():task.getClaimedDate().getTime())
                                : task.getCompletedDate().getTime())
//                设置任务ID，表明进行到哪一步了
                .sourceTaskId(ovTaskEntity.getTask_def_key_())
                .processKey(processTaskParams.getProcessKey())
//                设置处理信息
                .processMessage(processTaskParams.getMsg())
                .build();
        if(!endTask) {
            // 检查接收者是否为单个用户
            boolean isSingleUser = !toUsers.contains(",");
            String[] recipients = isSingleUser ? new String[]{toUsers} : toUsers.split(",");
            for (String recipient : recipients) {
                SmsEntity smsEntity = new SmsEntity();
                smsEntity.setId(UUID.randomUUID().toString());
                smsEntity.setProce_inst_id(task.getProcessInstanceId());
                smsEntity.setMsgCont("流程[" + processInstance.getName() + "][" + task.getName() + "]步骤已完成，目前需要您进行审核");
                smsEntity.setSender(sender);
                smsEntity.setState("0");
                smsEntity.setToUser(recipient);
                smsEntity.setSmsType("流程审核");
                smsEntity.setRdt(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                smsEntity.setTitle("您有一项流程待办需要审核");

                smsEntityMapper.addSms(smsEntity);
            }
            //                向租户端发送成功消息
            flowMessage.setStatusChangeId(StatusEnum.RUNNING.getStatusCode());
            flowMessage.setStatusChangeText(StatusEnum.RUNNING.getStatusName());
            flowMessage.setEnd(false);
            workFlowMessageContext.StorageMessageByTenant(flowMessage,ovTaskEntity.getTenant_id_());
        }
        else{
            SmsEntity smsEntity = new SmsEntity();
            smsEntity.setProce_inst_id(task.getProcessInstanceId());
            smsEntity.setId(UUID.randomUUID().toString());
            smsEntity.setMsgCont("您的["+processInstance.getName()+"]流程申请已经审核通过。");
            smsEntity.setSender(sender);
            smsEntity.setState("0");
            smsEntity.setToUser(historicProcessInstance.getStartUserId());
            smsEntity.setSmsType("流程通知");
            smsEntity.setRdt(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            smsEntity.setTitle("审核结果通知");
            smsEntityMapper.addSms(smsEntity);
            //                向租户端发送结转消息
            flowMessage.setStatusChangeId(StatusEnum.FINISH.getStatusCode());
            flowMessage.setStatusChangeText(StatusEnum.FINISH.getStatusName());
            flowMessage.setEnd(true);
            workFlowMessageContext.StorageMessageByTenant(flowMessage,ovTaskEntity.getTenant_id_());
        }
        return "发送成功";
    }

    @Override
    public void storeTenantStatusMessage(StatusEnum statusEnum, FlowMessage flowMessage, String tenant, String businessKey) {
        //                向租户端发送结转消息
        flowMessage.setStatusChangeId(statusEnum.getStatusCode());
        flowMessage.setStatusChangeText(statusEnum.getStatusName());
        workFlowMessageContext.StorageMessageByTenant(flowMessage,tenant);
    }

    /**
     * 发送抄送消息
     * @param processTaskParams
     * @return
     */
    @Override
    public String sendCopyForMsg(ProcessTaskParams processTaskParams){
        //知会数据
        List<ProcessTaskParams.CCData> ccDataList = Optional
                .ofNullable(processTaskParams.getCcData()).orElse(new ArrayList<>());
        if(ccDataList.size()<=0){
            return "没有设置抄送人员";
        }
        //当前登陆人
        String username = SecurityUtils.getUsername();
        //业务信息
        GenerWork generWork=generWorkMapper.getGenerWorkByInst(processTaskParams.getProcessInstanceId());
        for (ProcessTaskParams.CCData ccData: ccDataList) {
            SmsEntity smsEntity = new SmsEntity();
            smsEntity.setProce_inst_id(processTaskParams.getProcessInstanceId());
            smsEntity.setId(UUID.randomUUID().toString());
            smsEntity.setSender(username);
            smsEntity.setState("0");
            smsEntity.setToUser(ccData.getUsername());
            smsEntity.setSmsType("流程知会");
            smsEntity.setRdt(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            smsEntity.setTitle("关于[" + generWork.getTitle() + "]的办理情况");
            smsEntityMapper.addSms(smsEntity);
        }
        return "知会成功";
    }
    @Override
    public String addSms(SmsEntity smsEntity)
    {
        String msg="";
        try{
            smsEntityMapper.addSms(smsEntity);
            return "suess";
        }
        catch (Exception ex){
            return ex.getMessage();
        }
    }
    @Override
    public String updateState(String id){
        String msg="";
        try{
            smsEntityMapper.updateState(id);
            return "suess";
        }
        catch (Exception ex){
            return ex.getMessage();
        }
    }
    @Override
    public String deleteSms(String id){
        String msg="";
        try{
            smsEntityMapper.deleteSms(id);
            return "suess";
        }
        catch (Exception ex){
            return ex.getMessage();
        }
    }

}
