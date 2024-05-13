package com.activiti.z_six.service.manager;

import com.activiti.z_six.dto.controllerParams.*;
import com.activiti.z_six.entity.process.FlowEntity;
import com.activiti.z_six.entity.taskAssignee.AssigneeDSREntity;
import com.activiti.z_six.entity.taskAssignee.AssigneeUserEntity;
import com.activiti.z_six.entity.taskAssignee.FlowElementAttrs;
import com.activiti.z_six.entity.taskAssignee.OvProcessInstance;
import com.activiti.z_six.mapper.taskAssigneeMapper.AssigneeUserMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import com.activiti.z_six.util.StringUtils;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class IProcessDefinitionManager {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MapperManager mapperManager;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Autowired
    private AssigneeUserMapper assigneeUserMapper;
    private Long expireTime=Long.parseLong("360000") ;
    /**
     * 流程信息保存到redis
     * @return
     */
    public OvProcessInstance saveOvProcessInstance(String process_key, ProcessDefinitionParams params){
        if(redisUtils.exists(process_key)){
            redisUtils.remove(process_key);
        }
        redisUtils.set(process_key,JSON.toJSONString(params),expireTime);
        return null;
    }

    /**
     * 创建一个空的节点属性
     * @return
     */
    public FlowElementAttrs createFlowElementAttrs(){
        FlowElementAttrs flowElementAttrs=new FlowElementAttrs();
        flowElementAttrs.setRunWay("none");
        flowElementAttrs.setReturnWay("none");
        flowElementAttrs.setTransfer(0);
        flowElementAttrs.setCountersign(0);
        flowElementAttrs.setJumpWay("none");
        flowElementAttrs.setEndTask(0);
        flowElementAttrs.setRefuse(0);
        flowElementAttrs.setCcWay("none");
        flowElementAttrs.setAutoCCWay("none");
        return flowElementAttrs;
    }
    /**
     * 流程信息保存到redis
     * @return
     */
    public FlowElementAttrs saveFlowElementAttrs(String task_def_key, TaskDefinitionParams taskDefinitionParams){
        if(redisUtils.exists("UserTask_"+task_def_key)){
            redisUtils.remove("UserTask_"+task_def_key);
        }
//        System.out.println("写入数据:"+JSONObject.toJSONString(taskDefinitionParams));
        redisUtils.set("UserTask_"+task_def_key,JSON.toJSONString(taskDefinitionParams),expireTime);
        return null;
    }

    /**
     * 创建一个流程属性
     * @param process_key
     * @return
     */
    public ProcessDefinitionParams getParams(String process_key){
        ProcessDefinitionParams params=new ProcessDefinitionParams();
        //判断redis中是否存在
        if(redisUtils.exists(process_key)){
            params=JSONObject.parseObject(redisUtils.get(process_key).toString(),ProcessDefinitionParams.class);
        }
        else {
            params.setKey(process_key);
            //标题生成规则
            params.setTitleModel("");
            //流程业务分类
            params.setSortid("");
        }
        return params;
    }

    /**
     * 创建一个空的节点属性
     *
     * @param task_def_key
     * @param flowElementAttrs
     * @return
     */
    public TaskDefinitionParams getTaskDefinitionParams(String task_def_key, FlowElementAttrs flowElementAttrs){
        TaskDefinitionParams taskDefinitionParams=new TaskDefinitionParams();
//        System.out.println("进入方法");
        //判断redis中是否存在
        if(redisUtils.exists("UserTask_"+task_def_key)){
//            System.out.println("找到缓存:"+task_def_key);
            taskDefinitionParams=JSONObject.parseObject(redisUtils.get("UserTask_"+task_def_key).toString()
                    ,TaskDefinitionParams.class);
        }
        else {
            //创建一个空的基本信息
            FlowElementAttrs taskElementAttrs=createFlowElementAttrs();
            TaskModel taskModel = new TaskModel();
            BeanUtils.copyProperties(taskElementAttrs, taskModel);
            //组织数据
//            抽取这一部分数据
            if (flowElementAttrs != null){
//                System.out.println("即将更新数据："+JSONObject.toJSONString(taskModel));
//                System.out.println("即将更新数据："+JSONObject.toJSONString(flowElementAttrs));
                BeanUtils.copyProperties(flowElementAttrs, taskModel);
            }
//            System.out.println("即将写入数据："+JSONObject.toJSONString(taskModel));
            taskDefinitionParams.setFormModel(taskModel);
        }
//        System.out.println("即将写入数据："+JSONObject.toJSONString(taskDefinitionParams));
        return taskDefinitionParams;
    }

    /**
     * 创建节点
     * @param deploymentId
     */
    public void createTask(String deploymentId){
        OvProcessInstance ovProcessInstance=mapperManager.getProcessInsByDeployId(deploymentId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(ovProcessInstance.getId_());
        //获取流程信息
        Process process=bpmnModel.getProcesses().get(0);
        Collection<FlowElement> flowElements=process.getFlowElements();
        List<TaskDefinitionParams> userTasks=new ArrayList<>();
        for(FlowElement flowElement : flowElements){
            if (flowElement instanceof UserTask){
                //节点信息
                UserTask userTask=(UserTask) flowElement;
                String usertaskId=userTask.getId();
                //获取处理人规则
                String candidateUsers=userTask.getCandidateUsers().get(0);
                String ruleName=sequenceFlowManager.getRuleName(userTask.getId(),candidateUsers);

                //创建时，如果没有设置接收人，发布时报错,先将错误屏蔽
                try {
                    String assigneeString = "";
                    //读取缓存中设置的处理人数据
                    if (redisUtils.exists(usertaskId + "_" + ruleName)) {
                        assigneeString = redisUtils.get(usertaskId + "_" + ruleName).toString();
                        deleteAssigneeUserByTaskid(usertaskId);
//                        TODO:设置签入人
                        setAssigneeUser(assigneeString, usertaskId, ruleName);
                    }
                }
                catch (Exception ex){

                }
                Boolean isExists=true;
                FlowElementAttrs flowElementAttrs= mapperManager.getFlowElementAttrs(usertaskId);
                if(flowElementAttrs==null){
                    flowElementAttrs=createFlowElementAttrs();
                    flowElementAttrs.setId(UUID.randomUUID().toString());
                    flowElementAttrs.setTask_def_key(usertaskId);
                    flowElementAttrs.setProcess_key(ovProcessInstance.getKey_());
                    isExists=false;
                }
                if(!isExists){
                    mapperManager.setFlowElementAttrs(flowElementAttrs);
                }
                TaskDefinitionParams taskDefinitionParams=getTaskDefinitionParams(usertaskId,flowElementAttrs);
                userTasks.add(taskDefinitionParams);
            }
        }
        deployWithParams(ovProcessInstance.getKey_(),userTasks);
    }

    /**
     * 根据参数发布流程
     * @param process_key
     * @param userTasks
     */
    public void deployWithParams(String process_key,List<TaskDefinitionParams> userTasks){
        ProcessDefinitionParams params=getParams(process_key);
        params.setUserTasks(userTasks);
        updateDeployment(params);
    }

    /**
     * 配置更新
     * @param params
     * @return
     */
    public String updateDeployment(ProcessDefinitionParams params){
        updateProcessProperties(params);
        //设置节点信息
        List<TaskDefinitionParams> taskDefinitionParams=params.getUserTasks();
        for(TaskDefinitionParams taskParams:taskDefinitionParams){
            //节点功能设置
            TaskModel taskModel=taskParams.getFormModel();
            FlowElementAttrs taskElementAttrs=new FlowElementAttrs();
            BeanUtils.copyProperties(taskModel,taskElementAttrs);
            taskElementAttrs.setTask_def_key(taskParams.getId());
            taskElementAttrs.setProcess_key(params.getKey());
            taskElementAttrs.setRefuse(taskModel.getRefuse());
            taskElementAttrs.setEndTask(taskModel.getEndTask());
            taskElementAttrs.setCountersign(taskModel.getCountersign());
            taskElementAttrs.setTransfer(taskModel.getTransfer());
            mapperManager.updateFlowElementAttrs(taskElementAttrs);
        }

        return "执行成功";
    }

    /**
     * 更新流程属性
     * @param params
     */
    public void updateProcessProperties(ProcessDefinitionParams params){
        saveOvProcessInstance(params.getKey(),params);
        //更新流程部署信息，将引擎版本号作为流程类别
        FlowEntity flowEntity=new FlowEntity();
        flowEntity.setId_(params.getId());
        flowEntity.setName_(params.getName());
        flowEntity.setProcessKey(params.getKey());
        flowEntity.setEngine_version_(params.getSortid());
        flowEntity.setName_(params.getName());
        mapperManager.updateFlowEntity(flowEntity);

        //更新标题生成规则
        FlowElementAttrs flowElementAttrs=new FlowElementAttrs();
        flowElementAttrs.setProcess_key(params.getKey());
        flowElementAttrs.setTitleModel(params.getTitleModel());
        mapperManager.updateTitleModel(flowElementAttrs);

    }
    /**
     * 删除节点接收人
     * @param usertaskid
     * @return
     */
    public String deleteAssigneeUserByTaskid(String usertaskid){
        assigneeUserMapper.deleteAssigneeUserByTaskid(usertaskid);
        return "执行成功";
    }

    /**
     *配置某个任务节点处理人
     * @param assigneeList
     * @param usertaskid
     * @return
     */
    public String setAssigneeUser(String assigneeList, String usertaskid,String ruleName){
        JSONArray userlist = JSONObject.parseArray(assigneeList);
        List<AssigneeUserEntity> assigneeUserEntityList = Optional.ofNullable(userlist)
                .map(list->list.stream().map(it->{
                    JSONObject json = JSONObject.parseObject(JSON.toJSONString(it));
                    AssigneeUserEntity assigneeUserEntity = new AssigneeUserEntity();
                    if(SystemConfig.IsNullOrEmpty(json.getString("username"))){
                        assigneeUserEntity.setUsername(json.getString("id"));
                    }
                    else {
                        assigneeUserEntity.setUsername(json.getString("username"));
                    }
                    assigneeUserEntity.setUsertaskid(usertaskid);
                    assigneeUserEntity.setRuleName(ruleName);
                    assigneeUserEntity.setId(UUID.randomUUID().toString());
                    return assigneeUserEntity;
                }).collect(Collectors.toList())).orElse(new ArrayList<>());
//        TODO:扩展无具体指代人的找人方式挂载
        /*这里扩展找人方式，目前有按部门主管和部门直属领导的方式，在本节进行扩展。由于没有具体的指代人或者机构，上述循环代码不会执行*/
        List<String> extensionRule = Arrays.asList("byDeptMaster", "byDeptLeader");
        if (extensionRule.contains(ruleName)){
            assigneeUserEntityList.add(
                    new AssigneeUserEntity(UUID.randomUUID().toString(), usertaskid, "", "", ruleName, null)
            );
        }
        assigneeUserMapper.setAssigneeUser(assigneeUserEntityList);

        return "执行成功";
    }
    /**
     * 设置部门与岗位or角色交集
     * @param assigneeList
     * @param usertaskid
     * @return
     */
    public String setAssigneeByDeptAndStionOrRole(String assigneeList, String usertaskid){
        JSONArray userlist = JSONObject.parseArray(assigneeList);
        List<AssigneeDSREntity> assigneeDSREntities = Optional.ofNullable(userlist)
                .map(list-> list.stream().map(it->{
                            JSONObject json = JSONObject.parseObject(JSON.toJSONString(it));
                            AssigneeDSREntity assigneeDSREntity = new AssigneeDSREntity();
                            assigneeDSREntity.setDsr(json.getString("dsr"));
                            if(!StringUtils.isEmpty(json.getString("targetid")))
                                assigneeDSREntity.setTargetId(json.getString("targetid"));
                            assigneeDSREntity.setUsertaskid(usertaskid);
                            assigneeDSREntity.setId(UUID.randomUUID().toString());
                            return assigneeDSREntity;
                        })
                        .collect(Collectors.toList())).orElse(new ArrayList<>());
        assigneeUserMapper.setAssigneeByDeptAndStionOrRole(assigneeDSREntities);
        return "执行成功";
    }
}
