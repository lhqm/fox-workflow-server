package com.activiti.z_six.service.manager;

import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.entity.taskAssignee.*;
import com.activiti.z_six.entity.tenant.FlowProcess;
import com.activiti.z_six.mapper.taskAssigneeMapper.*;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.ISmsEntityService;
import com.activiti.z_six.templete.FindBpmModel;
import com.activiti.z_six.templete.FindWork;
import com.activiti.z_six.tenant.model.api.FlowMessage;
import com.activiti.z_six.tenant.model.TransMsgExtension;
import com.activiti.z_six.tenant.statusTrans.StatusEnum;
import com.activiti.z_six.util.DateUtils;
import com.activiti.z_six.util.SecurityUtils;
import com.activiti.z_six.util.SystemConfig;
import com.activiti.z_six.util.flow.FlowElementUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class IProcessTaskServiceManager {
    @Autowired
    private OvTaskEntityMapper ovTaskEntityMapper;
    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private TaskInstEntityMapper taskInstEntityMapper;
    @Autowired
    private ReturnWorkMapper returnWorkMapper;
    @Autowired
    private ReturnWayMapper returnWayMapper;
    @Autowired
    private ApprovalTrackMapper approvalTrackMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessRuntime processRuntime;
    @Autowired
    private GenerWorkMapper generWorkMapper;
    @Autowired
    private FlowElementAttrsMapper flowElementAttrsMapper;
    @Autowired
    private FindWork findWork;
    @Autowired
    private FindBpmModel findBpmModel;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ISmsEntityService smsEntityService;
    @Autowired
    private RepositoryService repositoryService;


    /**
     * 移交
     * @param processInstanceId
     * @param taskId
     * @param Msg
     * @param toUser
     * @return
     */
    public OvTaskEntity transfer(String processInstanceId, String taskId, String Msg,
                                 String toUser){
        try{
            OvTaskEntity task=ovTaskEntityMapper.ovTaskEntity(taskId);
            String assignee = task.getAssignee_();
            if(assignee ==null){
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(taskId).build());
            }

            OvTaskEntity ovTaskEntity = task;
            ovTaskEntity.setAssignee_(toUser);
            ovTaskEntity.setClaim_time_(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            //更新执行人
            ovTaskEntityMapper.setTaskStatus(ovTaskEntity);

            //增加轨迹
            this.addTaskInstTrack(processInstanceId,task,"transfer",Msg);

            ProcessTaskParams processTaskParams=new ProcessTaskParams();
            processTaskParams.setTaskId(taskId);
            processTaskParams.setProcessInstanceId(processInstanceId);
            processTaskParams.setMsg(Msg);
            this.setApprovalTrack(ovTaskEntity.getTask_def_key_(),ovTaskEntity.getName_(),processTaskParams,3,"移交");

//            发送消息给租户端
            sendMessageToTenant(processInstanceId,
                    new TransMsgExtension(SecurityUtils.getUsername(),toUser,Msg,ovTaskEntity.getName_()).getJsonString(),
                    task,StatusEnum.TRANSFER,false);

            return ovTaskEntity;
        }catch (Exception ex){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new OvTaskEntity();
        }
    }

    /**
     * 手动结束
     * @param processInstanceId
     * @param taskId
     * @param Msg
     * @param variables
     */
    public void setEndTask(String processInstanceId,String taskId,String Msg,
                           HashMap<String,Object> variables){
        OvTaskEntity ovTaskEntity=ovTaskEntityMapper.ovTaskEntity(taskId);
        try{
            //增加历史记录
            this.addTaskInstTrack(processInstanceId,ovTaskEntity,"setEndTask",Msg);
            ovTaskEntityMapper.delete(ovTaskEntity);

            //设置结束时间
            ovTaskEntityMapper.setPrcoInstStatus(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),processInstanceId);

            ProcessTaskParams processTaskParams=new ProcessTaskParams();
            processTaskParams.setVariables(variables);
            processTaskParams.setTaskId(taskId);
            processTaskParams.setProcessInstanceId(processInstanceId);
            processTaskParams.setMsg(Msg);
            this.setApprovalTrack(ovTaskEntity.getTask_def_key_(),ovTaskEntity.getName_(),processTaskParams,6,"手动结束");

            //            发送消息给租户端
            sendMessageToTenant(processInstanceId,Msg,ovTaskEntity,StatusEnum.INTERRUPT,true);
        }
        catch (Exception ex){
            ovTaskEntityMapper.setTaskStatus(ovTaskEntity);
        }
    }

    /**
     * 节点发送前
     * @param proc_inst_id
     * @param task_def_key
     * @param processTaskParams
     * @return
     */
    public SendActionDto sendBefore(String proc_inst_id
            ,String task_def_key,ProcessTaskParams processTaskParams,
                                    HashMap<String, Object> hashMap){
        SendActionDto sendActionDto=new SendActionDto();
        sendActionDto.setEndTask(false);
        //当前节点信息
        FlowElementAttrs flowElementAttrs=flowElementAttrsMapper
                .getFlowElementAttrs(task_def_key);
        sendActionDto.setAutoCCWay(flowElementAttrs.getAutoCCWay());
        sendActionDto.setCcWay(flowElementAttrs.getCcWay());
        return sendActionDto;
    }

    /**
     * 发送成功后
     * @param proc_inst_id
     * @param toUsers
     * @param processTaskParams
     * @return
     */
    public SendActionDto sendAfter(SendActionDto sendActionDto,String proc_inst_id,String toUsers
            ,ProcessTaskParams processTaskParams,HashMap<String, Object> hashMap){
        //获取下一步节点信息
        List<OvTaskEntity> ovTaskEntities=ovTaskEntityMapper
                .ovTaskEntityByProcessInsId(proc_inst_id);

        if(ovTaskEntities.size()==1){
            FlowElementAttrs flowElementAttrs=flowElementAttrsMapper
                    .getFlowElementAttrs(ovTaskEntities.get(0).getTask_def_key_());
            sendActionDto.setJumpWay(flowElementAttrs.getJumpWay());
            //下一步节点的id与名称
            sendActionDto.setTask_key(ovTaskEntities.get(0).getTask_def_key_());
            sendActionDto.setTaskName(ovTaskEntities.get(0).getName_());
        }
        if(ovTaskEntities.size()==0||ovTaskEntities==null)
            sendActionDto.setEndTask(true);
        return sendActionDto;
    }

    /**
     * 发送
     * @param processTaskParams
     * @param username
     * @return
     */
    public SendActionDto sendWork(ProcessTaskParams processTaskParams,String username){
        SendActionDto sendActionDto=new SendActionDto();
        //获取任务信息
        OvTaskEntity task = ovTaskEntityMapper.ovTaskEntity(processTaskParams.getTaskId());
        ProcessInstance processInstance = processRuntime.processInstance(task.getProc_inst_id_());

//        这行代码可以用来调试流程元素获取方法是否有问题。只需要在调试模式下发起对应流程的申请就行了
//        List<FlowProcess> definition = getFlowElementsByProcessDefinition(processInstance.getProcessDefinitionId());

        boolean isCanSend=false;
        //获取设置的流程参数
        HashMap<String, Object> VarHashMap=new HashMap<>();
        //节点提交之前事件
        SendActionDto sdto=this.sendBefore(task.getProc_inst_id_(),task.getTask_def_key_(), processTaskParams,VarHashMap);
        if(sdto.getState()==400){
            sendActionDto.setState(400);
            sendActionDto.setMsg("执行发送前事件错误，请检查配置");
            return sendActionDto;
        }
        else{
            sendActionDto.setCcWay(sdto.getCcWay());
            sendActionDto.setAutoCCWay(sdto.getAutoCCWay());
            sendActionDto.setCurTask_key(task.getTask_def_key_());
        }
        //判断是否是退回状态，以及退回信息
        ReturnWorkEntity returnWorkEntity=returnWorkMapper.getReturnWork(processTaskParams.getTaskId());
        if(returnWorkEntity!=null){
            if(returnWorkEntity.getReturnTo_act_id().equals(task.getTask_def_key_())){
                ReturnWayEntity returnWayEntity=returnWayMapper.getReturnWay(task.getTask_def_key_());
                //如果是一步步向下重新走一遍
                if(returnWayEntity.getRunWay().equals("none")){
                    isCanSend=true;
                }
                //如果是直接到达退回节点
                else{
                    isCanSend=false;
                    task.setId_(processTaskParams.getTaskId());
                    task.setName_(returnWorkEntity.getReturn_act_name());
                    task.setAssignee_(returnWorkEntity.getReturnUser());
                    task.setTask_def_key_(returnWorkEntity.getReturn_act_id());
                    ovTaskEntityMapper.setTaskStatus(task);
                }
            }
            else{
                isCanSend=true;
            }
        }
        else{
            isCanSend=true;
        }

        if(isCanSend) {
            //如果是待领取任务，先领取
            if (task.getAssignee_() == null) {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId_()).build());
            }
            /*
            可能存在：节点被用户手动选择路径。所以说这里需要先取出来备份
            该节点名字为nextNode
            进入下方findWork.getNextUserInfo()方法后，该值会由task_def_id变为用户选择列表
             */
            String nextNode=null;
//            存在即赋值。如果没有赋值成功就代表根本不是手动选择，不会进相关流程
            if (processTaskParams.getVariables()!=null && processTaskParams.getVariables().containsKey("nextNode")){
                nextNode=processTaskParams.getVariables().get("nextNode").toString();
            }
            this.setTitle(task.getProc_inst_id_(),processTaskParams.getBusinessKey(),task.getTask_def_key_());
            //获取下一步任务处理人
            HashMap<String, Object> variables = processTaskParams.getVariables();
            HashMap<String, Object> hashMap = findWork.getNextUserInfo(task.getProc_inst_id_(), task.getId_(), variables);
            //判断是否是手动选择人员发送
            try {
                if (!SystemConfig.IsNullOrEmpty(hashMap.get("byManuallySelect").toString())) {
                    sendActionDto.setState(200);
                    sendActionDto.setMsg(hashMap.get("byManuallySelect").toString());
                    sendActionDto.setTask_key(hashMap.get("nextNode").toString());
                    sendActionDto.setTaskId(task.getId_());
                    sendActionDto.setProc_inst_id(task.getProc_inst_id_());
                    return sendActionDto;
                }
            }//无需关注异常，说明不是手动选择发送
            catch (Exception ex){}
            //判断是否是手动选择发送路径
            try {
                if (!SystemConfig.IsNullOrEmpty(hashMap.get("condSelect").toString())) {
                    sendActionDto.setState(200);
                    sendActionDto.setMsg(hashMap.get("condSelect").toString());
                    sendActionDto.setTask_key(hashMap.get("nextNode"));
                    sendActionDto.setParams(hashMap.get("expType").toString());
                    sendActionDto.setTaskId(task.getId_());
                    sendActionDto.setProc_inst_id(task.getProc_inst_id_());
                    return sendActionDto;
                }
            }//无需关注异常，说明不是手动选择发送
            catch (Exception ex){}


            //执行发送
            Task taskObj = taskRuntime.complete(TaskPayloadBuilder.complete()
                    .withTaskId(task.getId_())
                    .withVariables(hashMap)
                    .build());
//            如果是手动选择路径流转，那么应该设置好流转路径的通知
            if (variables!=null && variables.containsKey("expType") && variables.get("expType").equals("ExclusiveGateway")){
                UserTask userTask = getUserTaskByTaskId(processTaskParams.getProcessInstanceId(), nextNode);
                FlowMessage flowMessage = FlowMessage.builder()
                        .processMessage("用户手动选择流程路径:" + userTask.getName())
                        .sourceTaskId(sendActionDto.getCurTask_key())
                        .processTime(System.currentTimeMillis())
                        .targetTaskId(userTask.getId())
                        .processKey(processTaskParams.getProcessKey())
                        .processInstanceId(taskObj.getProcessInstanceId())
                        .isEnd(false)
                        .build();
                smsEntityService.storeTenantStatusMessage(StatusEnum.HUMAN_MODEL_SELECTION,flowMessage,task.getTenant_id_());
            }
//            其他情况下的数据
//            如果是用户手动发送，那么送达时序应该是：
//            1.用户手动选择的消息报送
//            2.用户签发情况的消息到达
//            如果用户在手动选择的节点进行了拒签，那么送过来的应该是拒签数据，不再存在上述的消息报送和消息到达数据
            //增加审核记录
            this.setApprovalTrack(task.getTask_def_key_(),task.getName_(),processTaskParams,1,"审核");
            //下一步的接收人
            String msgToUsers=redisUtils.get(taskObj.getProcessInstanceId()+ "_sms").toString();

            sendActionDto=this.sendAfter(sendActionDto,taskObj.getProcessInstanceId(),msgToUsers,processTaskParams,VarHashMap);

            /**
             * 这里补了一下。
             * 原开源项目在流程结束分了子流程结束和总流程结束，子流程结束应该继续流转，但是总流程结束就应该直接结束了
             * 其划分子流程的写法过于粗暴，直接判断结束节点后边还有没有节点。
             * 实际上这在bpmn2.0里边是不正确的。因为子流程根本就不需要去包含结束节点
             * 具体代码见：com.activiti.z_six.strategy.sequenceFlow.EndEventStrategy.java
             */
            if(!SystemConfig.IsNullOrEmpty(msgToUsers) && !msgToUsers.equals("流程已结束")) {
                //发送消息
                smsEntityService.sendFlowMsg(msgToUsers,username,processInstance,processTaskParams,task,
                        taskObj,null,false);
            }
            else{
                //避免下一步选择人、或者没有接收人时报错，无法显示正常信息
                try {
//                    这里修改一下，流程结束应该要把流程通知返回给发起人，所以说这里不去使用msgToUsers,而是直接从流程去查流程发起人
//                    msgToUsers = redisUtils.get(taskObj.getProcessInstanceId() + "_end").toString();
                    if (!SystemConfig.IsNullOrEmpty(msgToUsers)) {
                        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                                .processInstanceId(taskObj.getProcessInstanceId())
                                .singleResult();
                        //发送消息
                        smsEntityService.sendFlowMsg(historicProcessInstance.getStartUserId(), username, processInstance,processTaskParams,task,
                                taskObj, historicProcessInstance, true);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return sendActionDto;
    }

    /**
     *设置标题
     * @param proce_inst_id
     * @param id
     * @param processKey
     * @return
     */
    public String setTitle(String proce_inst_id,String id,String processKey){
        try{
            //获取当前登陆人信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            //获取业务数据
            GenerWork generWork=generWorkMapper.generWork(id);
            FlowElementAttrs flowElementAttrs=new FlowElementAttrs();
            List<FlowElementAttrs> flowElementAttrsList=flowElementAttrsMapper.getFlowElementFormAttrs(processKey);
            if(flowElementAttrsList.size()>0){
                flowElementAttrs=flowElementAttrsList.get(0);
            }
            else{
                flowElementAttrs=flowElementAttrsMapper.getFlowElementAttrs(processKey);
            }
            //标题规则
            String titleModel=flowElementAttrs.getTitleModel();
            generWork.setId(id);
            if(SystemConfig.IsNullOrEmpty(generWork.getCreatetime()))
                generWork.setCreatetime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));

            //生成标题
            if(!SystemConfig.IsNullOrEmpty(titleModel)) {
                Map<String, Object> map = JSONObject.parseObject(generWork.getData_json());
                map.put("proce_inst_id",proce_inst_id);
                map.put("user_name",username);
                for (Map.Entry entry : map.entrySet()) {
                    if (titleModel.contains("@" + entry.getKey())) {
                        titleModel = titleModel.replace("@" + entry.getKey(), entry.getValue().toString());
                    }
                }
                generWork.setTitle(titleModel);
            }
            else{
                generWork.setTitle("无标题");
            }
            generWork.setProce_inst_id(proce_inst_id);
            generWorkMapper.updateFlowWork(generWork);
            return id;
        }
        catch (Exception ex){
            return ex.getMessage();
        }
    }
    /**
     * 增加运行轨迹
     * @param processInstanceId
     * @param ovTaskEntity
     * @param status
     * @param msg
     */
    public void addTaskInstTrack(String processInstanceId,OvTaskEntity ovTaskEntity,String status,
                                 String msg){
        String countersignMyPk= UUID.randomUUID().toString();
        try{
            //增加一条历史记录
            TaskInstEntity taskInstEntity=new TaskInstEntity();
            BeanUtils.copyProperties(ovTaskEntity,taskInstEntity);
            taskInstEntity.setId_(countersignMyPk);
            taskInstEntity.setProc_inst_id_(processInstanceId);
            taskInstEntity.setStart_time_(ovTaskEntity.getCreate_time_());
            taskInstEntity.setEnd_time_(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));

            //计算用时
            DateFormat startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate=startTime.parse(ovTaskEntity.getCreate_time_());
            Date endDate=startTime.parse(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            long duration=endDate.getTime()-startDate.getTime();

            taskInstEntity.setDuration_(duration);
            taskInstEntity.setTask_id_(ovTaskEntity.getId_());
            taskInstEntity.setAct_id_(ovTaskEntity.getTask_def_key_());
            taskInstEntity.setAct_name_(ovTaskEntity.getName_());
            //操作类型
            taskInstEntity.setAct_type_(status);
            //增加任务轨迹
            taskInstEntityMapper.addTaskInstTrack(taskInstEntity);
        }
        catch (Exception ex){
            taskInstEntityMapper.deleteInstTrack(countersignMyPk);
        }
    }

    /**
     * 增加审核意见
     * @param Task_def_key
     * @param Name
     * @param processTaskParams
     * @param actionType
     * @param actionName
     */
    public void setApprovalTrack(String Task_def_key,String Name,ProcessTaskParams processTaskParams,Integer actionType,String actionName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ApprovalTrack approvalTrack=new ApprovalTrack();
        BeanUtils.copyProperties(processTaskParams,approvalTrack);
        approvalTrack.setUuid(UUID.randomUUID().toString());
        approvalTrack.setUserTaskId(Task_def_key);
        approvalTrack.setUserTaskName(Name);
        approvalTrack.setUser(username);
        approvalTrack.setProcess_ints_id(processTaskParams.getProcessInstanceId());
        approvalTrack.setActionType(actionType);
        approvalTrack.setActionName(actionName);
        approvalTrack.setRdt(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
        approvalTrackMapper.addApprovalTrack(approvalTrack);
    }


    /**
     * 获取下一步节点信息
     * @param targetFlowElement
     * @param variables
     * @return
     */
    public List<UserTask> getNextTaskInfo(FlowElement targetFlowElement, HashMap<String,Object> variables){
        return findBpmModel.getNextTasks(variables,targetFlowElement);
    }

    /**
     * 根据流程实例ID和用户任务节点ID获取到详细的用户节点任务信息
     * @param processInstanceId 流程实例ID
     * @param taskId 流程节点ID
     * @return 用户任务的详细信息
     */

    public UserTask getUserTaskByTaskId(String processInstanceId,String taskId){
//        获取到流程的模型实例
        ProcessInstance processInstance = processRuntime.processInstance(processInstanceId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
//        筛选到流程路径定义，取到想要的UserTask数据
        Process process = bpmnModel.getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement element : flowElements) {
            if (element instanceof UserTask && taskId.equals(element.getId())){
                return (UserTask) element;
            }
        }
        return new UserTask();
    }

    /**
     * 为其他状态转移特意封装的通知消息类型。该方法用于快速从只具有参数列表内条件的状态迁移方法，固定迁移到指定的节点。
     * @param processInstanceId 流程实例ID
     * @param Msg 审批消息
     * @param task 任务节点ID
     * @param statusEnum 状态列举类型
     * @param endTask 是否结束
     */
    public void sendMessageToTenant(String processInstanceId,String Msg,OvTaskEntity task,StatusEnum statusEnum,boolean endTask){
        //            构造租户端移交消息
        FlowMessage flowMessage = FlowMessage.builder()
                .processInstanceId(processInstanceId)
                .processMessage(Msg)
                .processTime(System.currentTimeMillis())
                .targetTaskId(task.getTask_def_key_())
                .sourceTaskId(task.getTask_def_key_())
                .processKey(task.getProc_def_id_().split(":")[0])
                .isEnd(endTask)
                .build();
//            移交到租户端
        smsEntityService.storeTenantStatusMessage(statusEnum,flowMessage,task.getTenant_id_());
    }
}
