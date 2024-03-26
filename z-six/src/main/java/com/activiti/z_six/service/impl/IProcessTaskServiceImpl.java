package com.activiti.z_six.service.impl;

import com.activiti.z_six.SecurityUtil;
import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.dto.controllerParams.TaskManageParams;
import com.activiti.z_six.dto.controllerParams.TaskParams;
import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.entity.taskAssignee.*;
import com.activiti.z_six.mapper.orgmanagementMapper.UserEntityMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.*;
import com.activiti.z_six.service.IProcessTaskService;
import com.activiti.z_six.templete.FindWork;
import com.activiti.z_six.tenant.service.WorkFlowMessageContext;
import com.activiti.z_six.tenant.model.api.FlowMessage;
import com.activiti.z_six.tenant.model.TransMsgExtension;
import com.activiti.z_six.tenant.statusTrans.StatusEnum;
import com.activiti.z_six.util.SystemConfig;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.runtime.api.model.impl.APIProcessInstanceConverter;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.activiti.z_six.service.manager.IProcessTaskServiceManager;

import java.util.*;

@Service
public class IProcessTaskServiceImpl implements IProcessTaskService {
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired //流程实例服务
    private ProcessRuntime processRuntime;
    @Autowired //任务执行服务
    private TaskRuntime taskRuntime;
    @Autowired //自定义
    private OvTaskEntityMapper ovTaskEntityMapper;
    @Autowired //流程实例服务
    private OvProcessInstanceMapper ovProcessInstanceMapper;
    @Autowired //任务服务
    private TaskService taskService;
    @Autowired //历史任务服务
    private HistoryService historyService;
    @Autowired
    private AssigneeUserMapper assigneeUserMapper;
    @Autowired //流程发布信息服务
    private RepositoryService repositoryService;
    @Autowired //流程运行实例服务
    private RuntimeService runtimeService;
    @Autowired //用户服务
    private UserEntityMapper userEntityMapper;
    @Autowired //退回信息
    private ReturnWorkMapper returnWorkMapper;
    @Autowired //退回设置
    private ReturnWayMapper returnWayMapper;
    @Autowired
    private FlowElementAttrsMapper flowElementAttrsMapper;
    @Autowired
    private GenerWorkMapper generWorkMapper;
    @Autowired
    private IProcessTaskServiceManager processTaskServiceManager;
    @Autowired
    private FindWork findWork;
    @Autowired
    private WorkFlowMessageContext workFlowMessageContext;
    @Autowired
    private APIProcessInstanceConverter processInstanceConverter;

    /**
     * 获取任务表单内容
     * @param processKey
     * @param taskid
     * @return
     */
    @Override
    public HashMap<String, Object> getHisFormJson(String processKey,String taskid,String userName){
        HashMap<String, Object> hashMap=new HashMap<>();
        try{
            //没有taskid说明是开始节点
            if(SystemConfig.IsNullOrEmpty(taskid)){
                //获取当前登录用户信息
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                System.out.println("authentication = " + authentication);
                String username=authentication==null?userName:authentication.getName();
                securityUtil.logInAs(username);
//                System.out.println("authentication = " + SecurityContextHolder.getContext().getAuthentication());

                //获取流程模版信息
                OvProcessInstance ovProcessInstance=ovProcessInstanceMapper.getProcessInsLastVersion(processKey);
                BpmnModel bpmnModel = repositoryService.getBpmnModel(ovProcessInstance.getId_());
                //获取流程信息
                Process process=bpmnModel.getProcesses().get(0);
                StartEvent startEvent=(StartEvent)process.getInitialFlowElement();
                List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
                //获取UserTask类型数据
                UserTask userTask = (UserTask) outgoingFlows.get(0).getTargetFlowElement();
                FlowElementAttrs flowElementAttrs=flowElementAttrsMapper.getFlowElementAttrs(userTask.getId());

                hashMap.put("mapJson",flowElementAttrs.getForm_url());
                hashMap.put("form_type",flowElementAttrs.getForm_type());
                hashMap.put("form_url",flowElementAttrs.getForm_url());
            }
            else{
                //获取任务信息
                OvTaskEntity task = ovTaskEntityMapper.ovTaskEntity(taskid);
                FlowElementAttrs flowElementAttrs=flowElementAttrsMapper.getFlowElementAttrs(task.getTask_def_key_());

                hashMap.put("mapJson",flowElementAttrs.getForm_url());
                hashMap.put("form_type",flowElementAttrs.getForm_type());
                hashMap.put("form_url",flowElementAttrs.getForm_url());
            }
            return hashMap;
        }
        catch (Exception ex){
            return hashMap;
        }
    }

    /**
     * 流程启动
     * @param processTaskParams
     * @return
     */
    @Override
    public SendActionDto startProcess(ProcessTaskParams processTaskParams,String username){
        SendActionDto sendActionDto=new SendActionDto();
        //关联表单业务的主键，可通过参数传递过来，也可以自动生成
        String businessKey=processTaskParams.getBusinessKey();
        //启动之前，进行判断
        OvProcessInstance ovProcessInstance=ovProcessInstanceMapper.getProcessInsLastVersion(processTaskParams.getProcessKey());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(ovProcessInstance.getId_());
        //获取流程信息
        Process process=bpmnModel.getProcesses().get(0);
        StartEvent startEvent=(StartEvent)process.getInitialFlowElement();
        List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
        if(outgoingFlows.size()>1){
            sendActionDto.setState(400);
            sendActionDto.setMsg("发起流程失败,启动事件后面，只能有一个任务节点");
            return sendActionDto;
        }
        //获取用户任务
        UserTask userTask = (UserTask) outgoingFlows.get(0).getTargetFlowElement();
        //填入用户变量
        HashMap<String, Object> vars = new HashMap<>();
        vars.put(userTask.getId()+"_byStarter",username);
        //以多租户形式启动实例
        /**
         * 这里的代码经过了修正，因为activiti的启动流程实例，没有提供租户的参数，所以这里通过activiti源码进行参照修改
         * 下层接口中有租户相关实现和升级流程为可挂起的代码，直接借鉴过来使用就行了
         */
//        TODO:修改流程启动为多租户模式
        ProcessInstance processInstance = processInstanceConverter.from(runtimeService.createProcessInstanceBuilder()
                .tenantId(processTaskParams.getTenantId() == null ? "main" : processTaskParams.getTenantId())
                .businessKey(businessKey)
                .processDefinitionKey(processTaskParams.getProcessKey())
                .variables(vars)
                .name(processTaskParams.getProcessName()).start());
//        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
//                .start()
//                .withProcessDefinitionKey(processTaskParams.getProcessKey())
//                .withName(processTaskParams.getProcessName())
//                .withVariable(userTask.getId()+"_byStarter",username)
//                .withBusinessKey(businessKey)
//                .build());

//        推送流程启动的消息
        List<FlowElement> flowElements = (List<FlowElement>) process.getFlowElements();

        FlowMessage flowMessage = FlowMessage.builder()
                .processInstanceId(processInstance.getId())
//                将用户启动动作当做用户审批消息发出
                .processMessage("用户["+username+"]发起流程")
//                截取当前时间作为启动标注
                .processTime(System.currentTimeMillis())
                .isEnd(false)
//                下一个节点的ID
                .targetTaskId(userTask.getId())
//                源ID设为流程的启动节点ID
                .sourceTaskId(flowElements.get(0).getId())
                .processKey(processTaskParams.getProcessKey())
//                设置启动状态
                .statusChangeId(StatusEnum.STARTING.getStatusCode())
                .statusChangeText(StatusEnum.STARTING.getStatusName())
                .businessKey(businessKey)
                .build();
//        发送到Redis的消息队列
        workFlowMessageContext.StorageMessageByTenant(flowMessage, processTaskParams.getTenantId());
        //获取任务信息
        org.activiti.engine.task.Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskCandidateOrAssigned(username)
                .singleResult();

        if(processTaskParams.getVariables()==null){
            HashMap<String,Object> variables=new HashMap<>();
            variables.put(userTask.getId()+"_byStarter",username);
            processTaskParams.setVariables(variables);
        }

        processTaskParams.setTaskId(task.getId());
        processTaskParams.setProcessInstanceId(processInstance.getId());
        return this.sendWork(processTaskParams);
    }

    @Override
    public SendActionDto startProcess(ProcessTaskParams processTaskParams) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        //获取当前登录用户信息
        securityUtil.logInAs(username);
        return startProcess(processTaskParams,username);
    }

    /**
    发送到下一步
     */
    @Override
    public SendActionDto sendWork(ProcessTaskParams processTaskParams){
        //获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        securityUtil.logInAs(username);

        return processTaskServiceManager.sendWork(processTaskParams,username);
    }

    /**
     * 驳回
     * @param returnWork
     * @return
     */
    @Override
    public OvTaskEntity returnWork(ReturnWorkEntity returnWork){
        String re_id=UUID.randomUUID().toString();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            //查询历史数据，获取节点名称
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(returnWork.getProc_inst_id())
                    .taskDefinitionKey(returnWork.getReturnTo_act_id()).orderByHistoricTaskInstanceStartTime().desc().list();

            HistoricTaskInstance historicTaskInstance=historicTaskInstances.get(0);
            //获取退回节点信息
            OvTaskEntity ovTaskEntity = ovTaskEntityMapper.ovTaskEntity(returnWork.getTaskid());
            OvTaskEntity taskEntity=ovTaskEntity;
            //获取退回设置
            ReturnWayEntity returnWayEntity=returnWayMapper.getReturnWay(ovTaskEntity.getTask_def_key_());

            String task_def_key=ovTaskEntity.getTask_def_key_();
            String task_def_name=ovTaskEntity.getName_();
            //增加退回记录
            ReturnWorkEntity returnWorkEntity = new ReturnWorkEntity();
            BeanUtils.copyProperties(returnWork,returnWorkEntity);
            returnWorkEntity.setId(re_id);
            returnWorkEntity.setProc_def_id(ovTaskEntity.getProc_def_id_());
            returnWorkEntity.setProc_inst_id(ovTaskEntity.getProc_inst_id_());
            returnWorkEntity.setReturn_act_id(ovTaskEntity.getTask_def_key_());
            returnWorkEntity.setReturn_act_name(ovTaskEntity.getName_());
            returnWorkEntity.setReturnTo_act_name(historicTaskInstance.getName());
            returnWorkEntity.setReturnUser(username);
            returnWorkEntity.setReturn_time(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));

            //如果退回后，需要重新走一遍，则删除，否则，不删除
            if(returnWayEntity.getRunWay().equals("none")) {
                //删除其他分支任务
                List<OvTaskEntity> taskList = ovTaskEntityMapper.ovTaskEntityByProcessInsId(returnWork.getProc_inst_id());
                for (OvTaskEntity task : taskList) {
                    if (task.getTask_def_key_().equals(ovTaskEntity.getTask_def_key_()))
                        continue;
                    else {
                        ovTaskEntityMapper.delete(task);
                    }
                }
            }
            ovTaskEntity.setId_(returnWork.getTaskid());
            ovTaskEntity.setName_(historicTaskInstance.getName());
            ovTaskEntity.setAssignee_(historicTaskInstance.getAssignee());
            ovTaskEntity.setExecution_task_def_key_(ovTaskEntity.getTask_def_key_());
            ovTaskEntity.setTask_def_key_(returnWorkEntity.getReturnTo_act_id());

            //增加退回记录
            returnWorkMapper.createReturnDetailed(returnWorkEntity);
            //更新执行节点与执行人
            ovTaskEntityMapper.setTaskStatusToReturn(ovTaskEntity);
            //增加轨迹
            processTaskServiceManager.addTaskInstTrack(returnWork.getProc_inst_id(),taskEntity,"returnWork",returnWork.getReturn_msg());

            ProcessTaskParams processTaskParams=new ProcessTaskParams();
            //processTaskParams.setVariables(returnWork.getVariables());
            processTaskParams.setTaskId(returnWork.getTaskid());
            processTaskParams.setProcessInstanceId(returnWork.getProc_inst_id());
            processTaskParams.setMsg(returnWork.getReturn_msg());
            processTaskServiceManager.setApprovalTrack(task_def_key,task_def_name,processTaskParams,2,"驳回");
//            获取到这条流程实例
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(returnWork.getProc_inst_id()).singleResult();
            //记录日志
            //        推送流程被拒绝的消息
            FlowMessage flowMessage = FlowMessage.builder()
                    .processInstanceId(returnWork.getProc_inst_id())
//                将驳回消息封装送回
                    .processMessage(returnWork.getReturn_msg())
//                截取当前时间作为启动标注
                    .processTime(System.currentTimeMillis())
                    .isEnd(false)
//                下一个节点的ID
                    .targetTaskId(returnWayEntity.getTask_def_id())
//                源ID设为流程的启动节点ID
                    .sourceTaskId(returnWork.getTaskid())
                    .processKey(taskEntity.getProc_def_id_().split(":")[0])
//                设置拒绝的状态
                    .statusChangeId(StatusEnum.REFUSE.getStatusCode())
                    .statusChangeText(StatusEnum.REFUSE.getStatusName())
                    .businessKey(processInstance.getBusinessKey())
                    .build();
//        发送到Redis的消息队列
            workFlowMessageContext.StorageMessageByTenant(flowMessage,ovTaskEntityMapper.ovTaskEntity(processTaskParams.getTaskId()).getTenant_id_());

            return ovTaskEntity;
        }
        catch (Exception ex){
            returnWorkMapper.deleteReturn(re_id);
            return new OvTaskEntity();
        }
    }

    /**
     * 移交
     * @param params
     * @return
     */
    @Override
    public OvTaskEntity transfer(TaskManageParams params){
        OvTaskEntity ovTaskEntity= processTaskServiceManager.transfer(params.getProc_inst_id(),
                params.getTaskid(),params.getMsg(),params.getToUser());
        return ovTaskEntity;
    }

    /**
     * 批量移交
     * @param param
     * @return
     */
    @Override
    public String transferBatch(TaskManageParams param){
        List<TaskParams> taskParams=param.getTaskParams();
        String msg= param.getMsg();
        String toUser= param.getToUser();
        for(TaskParams tp:taskParams){
            processTaskServiceManager.transfer(tp.getProce_inst_id(),
                    tp.getTaskid(),msg,toUser);
        }
        return "执行成功";
    }
    /**
     * 加签
     * @param params
     * @return
     */
    @Override
    public OvTaskEntity countersign(TaskManageParams params){
        try{
            OvTaskEntity task=ovTaskEntityMapper.ovTaskEntity(params.getTaskid());
            String assignee = task.getAssignee_();
            if(assignee ==null){
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(params.getTaskid()).build());
            }

            OvTaskEntity ovTaskEntity = task;
            ovTaskEntity.setAssignee_(params.getToUser());
            ovTaskEntity.setClaim_time_(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));

            //执行加签
            taskRuntime.addCandidateUsers(TaskPayloadBuilder.addCandidateUsers().withTaskId(params.getTaskid())
                    .withCandidateUser(params.getToUser()).build());
            //更新执行人
            ovTaskEntityMapper.setTaskStatus(ovTaskEntity);
            //增加轨迹
            processTaskServiceManager.addTaskInstTrack(params.getProc_inst_id(),task,"countersign", params.getMsg());

            ProcessTaskParams processTaskParams=new ProcessTaskParams();
            processTaskParams.setTaskId(params.getTaskid());
            processTaskParams.setProcessInstanceId(params.getProc_inst_id());
            processTaskParams.setMsg(params.getMsg());
            processTaskServiceManager.setApprovalTrack(ovTaskEntity.getTask_def_key_(),ovTaskEntity.getName_(),processTaskParams,4,"加签");
            HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(params.getProc_inst_id()).singleResult();

            //记录日志
            //        推送流程加签的消息
            FlowMessage flowMessage = FlowMessage.builder()
                    .processInstanceId(params.getProc_inst_id())
//                将加签消息封装送回
                    .processMessage(new TransMsgExtension(assignee,ovTaskEntity.getAssignee_(),params.getMsg(),task.getName_()).getJsonString())
//                截取当前时间作为加签节点标注
                    .processTime(System.currentTimeMillis())
                    .isEnd(false)
//                下一个节点的ID，加签属于原地打转，直接上本流程ID就行
                    .targetTaskId(ovTaskEntity.getTask_def_key_())
//                源ID设为流程的当前节点ID，加签后，流程依旧卡在这个节点上
                    .sourceTaskId(ovTaskEntity.getTask_def_key_())
                    .processKey(ovTaskEntity.getProc_def_id_().split(":")[0])
//                设置加签的状态
                    .statusChangeId(StatusEnum.COUNTERSIGN.getStatusCode())
                    .statusChangeText(StatusEnum.COUNTERSIGN.getStatusName())
                    .businessKey(instance.getBusinessKey())
                    .build();
//        发送到Redis的消息队列
            workFlowMessageContext.StorageMessageByTenant(flowMessage,ovTaskEntityMapper.ovTaskEntity(processTaskParams.getTaskId()).getTenant_id_());
            return ovTaskEntity;
        }catch (Exception ex){
            return new OvTaskEntity();
            //mybatis数据库回滚事物，TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 不同意
     * @param params
     */
    @Override
    public void refuse(TaskManageParams params){
        OvTaskEntity ovTaskEntity=ovTaskEntityMapper.ovTaskEntity(params.getTaskid());
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            ovTaskEntity.setAssignee_(username);

            //增加历史记录
            processTaskServiceManager.addTaskInstTrack(params.getProc_inst_id(), ovTaskEntity,"refuse", params.getMsg());
            ovTaskEntityMapper.delete(ovTaskEntity);
            ovTaskEntityMapper.setPrcoInstReason("refuse",params.getProc_inst_id());

            //设置结束时间
            ovTaskEntityMapper.setPrcoInstStatus(DateTime.now().toString("YYYY-MM-dd HH:mm:ss"),params.getProc_inst_id());
            ProcessTaskParams processTaskParams=new ProcessTaskParams();
            processTaskParams.setTaskId(params.getTaskid());
            processTaskParams.setProcessInstanceId(params.getProc_inst_id());
            processTaskParams.setMsg(params.getMsg());
            processTaskServiceManager.setApprovalTrack(ovTaskEntity.getTask_def_key_(),ovTaskEntity.getName_(),processTaskParams,5,"不同意");
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(params.getProc_inst_id()).singleResult();
            //        推送流程驳回的消息
            FlowMessage flowMessage = FlowMessage.builder()
                    .processInstanceId(params.getProc_inst_id())
//                将用户启动动作当做用户审批消息发出
                    .processMessage(params.getMsg())
//                截取当前时间作为启动标注
                    .processTime(System.currentTimeMillis())
//                    .isEnd(false)
//                下一个节点的ID
                    .targetTaskId(null)
//                源ID设为流程的启动节点ID
                    .sourceTaskId(ovTaskEntity.getTask_def_key_())
                    .processKey(ovTaskEntity.getProc_def_id_().split(":")[0])
//                设置启动状态
                    .statusChangeId(StatusEnum.REFUSE.getStatusCode())
                    .statusChangeText(StatusEnum.REFUSE.getStatusCode())
                    .businessKey(processInstance.getBusinessKey())
                    .build();
//        发送到Redis的消息队列
            workFlowMessageContext.StorageMessageByTenant(flowMessage,ovTaskEntity.getTenant_id_());
        }
        catch (Exception ex){
            ovTaskEntityMapper.setTaskStatus(ovTaskEntity);
        }
    }

    /**
     * 手动结束
     * @param params
     */
    @Override
    public void setEndTask(TaskManageParams params){
        processTaskServiceManager.setEndTask(params.getProc_inst_id(),
                params.getTaskid(), params.getMsg(), new HashMap<>());
    }
    /**
     * 批量结束流程
     * @param params
     * @return
     */
    @Override
    public String setEndTaskBatch(TaskManageParams params){
        List<TaskParams> taskParams=params.getTaskParams();
        String msg= params.getMsg();
        for(TaskParams tp:taskParams){
            processTaskServiceManager.setEndTask(tp.getProce_inst_id(),
                    tp.getTaskid(),msg,new HashMap<>());
        }
        return "执行成功";
    }

    /**
     * 获取审核按钮
     * @param taskid
     * @return
     */
    @Override
    public FlowElementAttrs getElementButton(String taskid){
        OvTaskEntity task= ovTaskEntityMapper.ovTaskEntity(taskid);
        return flowElementAttrsMapper.getFlowElementAttrs(task.getTask_def_key_());
    }
    /**
     * 删除流程实例
     * @param params
     */
    @Override
    public void deleteProcInst(TaskManageParams params){
        OvTaskEntity ovTaskEntity=ovTaskEntityMapper.ovTaskEntity(params.getTaskid());
        try{
            //增加历史记录
            processTaskServiceManager.addTaskInstTrack(params.getProc_inst_id(), ovTaskEntity,"deleteTask", params.getMsg());
            ovTaskEntityMapper.setPrcoInstReason("deleteTask",params.getProc_inst_id());

            //设置结束时间
            ovTaskEntityMapper.setPrcoInstStatus(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),params.getProc_inst_id());
        }
        catch (Exception ex){
            ovTaskEntityMapper.setTaskStatus(ovTaskEntity);
        }
    }
    @Override
    public String sendReceiceTask(String processInstanceId, String taskKey, String executionId){
        //获取流程定义模版
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstanceId);
        //获取流程信息
        FlowElement flowElement = bpmnModel.getFlowElement(taskKey);
        ReceiveTask receiveTask=(ReceiveTask) flowElement;
        List<SequenceFlow> outgoingFlows = receiveTask.getOutgoingFlows();
        //获取用户任务，获取下一步到达的节点，也可能是网关
        FlowElement targetFlowElement = outgoingFlows.get(0).getTargetFlowElement();

        //获取任务信息
        HashMap<String,Object> variables=new HashMap<>();
        variables=findWork.getNextUsers(variables,targetFlowElement,processInstanceId);
        //向等待节点发送消息
        runtimeService.setVariables(executionId,variables);
        runtimeService.trigger(executionId);
        return "发送成功";
    }
    /**
     * 获取下一步节点信息
     * @param procInstId
     * @param taskid
     * @param variables
     * @return
     */
    @Override
    public List<UserTask> getNextTaskInfo(String procInstId,String taskid,HashMap<String,Object> variables){
                List<UserTask> userTaskList=new ArrayList<>();
        try{
            //获取当前流程信息
            String processDefinitionId = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInstId)
                    .singleResult()
                    .getProcessDefinitionId();
            //获取当前任务信息
            org.activiti.engine.task.Task task=taskService.createTaskQuery().taskId(taskid).singleResult();

            //获取流程定义模版
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            //获取流程信息
            FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
            //获取用户任务
            UserTask userTask = (UserTask) flowElement;
            //获取下一步到达的节点，也可能是网关
            List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
            //下一步必须是一个节点或网关，不支持不通过网关就到达多个节点的模式，因此只取第一个对象
            // todo:不通过网关就到达多个节点的模式待认证
            FlowElement targetFlowElement = outgoingFlows.get(0).getTargetFlowElement();
            userTaskList=processTaskServiceManager.getNextTaskInfo(targetFlowElement,variables);
            //返回下一步节点
            return userTaskList;
        }
        catch(Exception ex) {
            return userTaskList;
        }
    }
    /**
     * 获取下一步处理人
     * @param procInstId
     * @param taskid
     * @param variables
     * @return
     */
    @Override
    public HashMap<String,Object> getNextUserInfo(String procInstId,String taskid,HashMap<String,Object> variables){
        return findWork.getNextUserInfo(procInstId,taskid,variables);
    }

    /**
     * 获取流程待办
     * @param pageNum
     * @param pagesize
     * @param flowName
     * @return
     */
    @Override
    public HashMap<String,Object> getTodoList(Integer pageNum,Integer pagesize,String flowName){
        //获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        List<GenerWork> generWorkList=generWorkMapper.getTodoList(username,pageNum,pagesize,flowName);
        List<GenerWork> generWorkNum=generWorkMapper.getTodoListNum(username,flowName);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("list",generWorkList);
        hashMap.put("total",generWorkNum.size());
        return hashMap;
    }
    @Override
    public HashMap<String,Object> getTodoTask(String flowSort, String proceId, Integer page, Integer pagesize){
        Integer startIndex=0;
        Integer maxltems=0;
        if(page==1) {
            startIndex = 0;
            maxltems=pagesize;
        }
        else{
            startIndex=pagesize*(page-1);
            maxltems=page*page;
        }
        //获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        securityUtil.logInAs(username);
        //获取分页数据
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(startIndex, maxltems));
        //构建返回数据
        List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();

        for (Task tk : tasks.getContent()) {
            //用户获取流程名称
            ProcessInstance processInstance = processRuntime.processInstance(tk.getProcessInstanceId());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", tk.getId());//taskid，类似workid
            hashMap.put("name", tk.getName());//任务节点名称
            hashMap.put("status", tk.getStatus());//任务状态
            hashMap.put("createdDate", tk.getCreatedDate());//任务创建时间，实际就是任务到达时间
            if (tk.getAssignee() == null) {//执行人，null时前台显示未拾取
                hashMap.put("assignee", "待拾取任务");
            } else {
                hashMap.put("assignee", tk.getAssignee());//执行人
            }
            hashMap.put("procinstid",tk.getProcessInstanceId());
            hashMap.put("instanceName", processInstance.getName());
            listMap.add(hashMap);
        }

        HashMap<String,Object> taskMap=new HashMap<>();
        taskMap.put("list",listMap);
        taskMap.put("total",listMap.size());

        return taskMap;
    }

    /**
     * 获取已办列表
     * @param
     * @param
     * @param
     * @param pagesize
     * @return
     */
    @Override
    public HashMap<String, Object> getHistoricTaskInstance(String flowName, Integer pageNum
            , Integer pagesize){
        Integer startIndex=0;
        if(pageNum==1) {
            startIndex = 0;
        }
        else{
            startIndex=pagesize*(pageNum-1);
        }
        //获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        List<GenerWork> generWorkList=generWorkMapper.getDoneList(username,startIndex,pagesize,flowName);
        List<GenerWork> generWorkNum=generWorkMapper.getDoneListNum(username,flowName);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("list",generWorkList);
        hashMap.put("total",generWorkNum.size());
        return hashMap;
    }

    @Override
    public HashMap<String, Object> getHisFormJson(String processKey, String taskid) {
        return getHisFormJson(processKey,taskid,null);
    }

    @Override
    public String setTitle(String proce_inst_id,String id,String processKey){
        return processTaskServiceManager.setTitle(proce_inst_id,id,processKey);
    }
    /**
     * 获取手动选择接收人列表
     * @param bmsType
     * @param task_def_id
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public HashMap<String,Object>  getSelectNextUsers(String bmsType, String task_def_id,
                                        Integer page, Integer pagesize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<AssigneeDSREntity> assigneeDSREntities=assigneeUserMapper.getAssigneeDSR(task_def_id);
        Integer startIndex=(page-1)*pagesize;
        if(bmsType.equals("byManuallySelectDept")){
            AssigneeDSREntity assigneeDSREntity=assigneeDSREntities.get(0);
            List<UserEntity> userEntities=userEntityMapper.getUserByDepartId(Integer.parseInt(assigneeDSREntity.getTargetId()));
            List<UserEntity> userEntityList=userEntityMapper.getUserByDepartIdPage(Integer.parseInt(assigneeDSREntity.getTargetId()),
                    startIndex,pagesize);
            hashMap.put("total",userEntities.size());
            hashMap.put("list",userEntityList);
        }
        else if(bmsType.equals("byManuallySelectRole")){
            Integer pageTotal=0;
            List<UserEntity> allUserEntityList=new ArrayList<>();
            for(AssigneeDSREntity assigneeDSREntity:assigneeDSREntities) {
                List<UserEntity> userEntities = userEntityMapper.getUserByRoleId(assigneeDSREntity.getTargetId());
                List<UserEntity> userEntityList = userEntityMapper.getUserByRoleIdPage(assigneeDSREntity.getTargetId(),
                        startIndex, pagesize);
                pageTotal+=userEntities.size();
                allUserEntityList.addAll(userEntityList);
            }
            hashMap.put("total", pageTotal);
            hashMap.put("list", allUserEntityList);
        }
        else if(bmsType.equals("byManuallySelectStion")){
            Integer pageTotal=0;
            List<UserEntity> allUserEntityList=new ArrayList<>();
            for(AssigneeDSREntity assigneeDSREntity:assigneeDSREntities) {
                List<UserEntity> userEntities = userEntityMapper.getUserByPositionId(assigneeDSREntity.getTargetId());
                List<UserEntity> userEntityList = userEntityMapper.getUserByPositionIdPage(assigneeDSREntity.getTargetId(),
                        startIndex, pagesize);
                pageTotal+=userEntities.size();
                allUserEntityList.addAll(userEntityList);
            }
            hashMap.put("total", pageTotal);
            hashMap.put("list", allUserEntityList);
        }
        return hashMap;
    }

    /**
     * 设置下一步接收人（手动选择有效）
     * @param manuallySelectEntityList
     * @return
     */
    @Override
    public String setNextToUsers(List<ManuallySelectEntity> manuallySelectEntityList){
        List<ManuallySelectEntity> manuallySelectEntities=new ArrayList<>();
        String task_def_id="";
        String proc_inst_id="";
        for(ManuallySelectEntity userEntity:manuallySelectEntityList){
            ManuallySelectEntity manuallySelect=new ManuallySelectEntity();
            manuallySelect.setId(UUID.randomUUID().toString());
            manuallySelect.setUsername(userEntity.getUsername());
            manuallySelect.setTask_def_id(userEntity.getTask_def_id());
            manuallySelect.setProc_inst_id(userEntity.getProc_inst_id());
            manuallySelectEntities.add(manuallySelect);
            task_def_id=userEntity.getTask_def_id();
            proc_inst_id=userEntity.getProc_inst_id();
        }
        assigneeUserMapper.deleteAssigneeManuallySelect(task_def_id,proc_inst_id);
        assigneeUserMapper.setAssigneeByManuallySelect(manuallySelectEntities);
        return "执行成功";
    }
}
