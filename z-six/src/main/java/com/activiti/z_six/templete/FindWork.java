package com.activiti.z_six.templete;

import com.activiti.z_six.strategy.sequenceFlow.SequenceFlowContext;
import com.activiti.z_six.strategy.sequenceFlow.SequenceFlowStrategy;
import com.activiti.z_six.strategy.userByTaskRule.UserByTaskRuleContext;
import com.activiti.z_six.strategy.userByTaskRule.UserByTaskRuleStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindWork {

    @Autowired //历史任务服务
    private HistoryService historyService;
    @Autowired //任务服务
    private TaskService taskService;
    @Autowired //流程发布信息服务
    private RepositoryService repositoryService;
    @Autowired
    private UserByTaskRuleContext userByTaskRuleContext;
    @Autowired
    private SequenceFlowContext sequenceFlowContext;

    /**
     * 获取下一步接收人
     * @param procInstId
     * @param taskid
     * @param variables
     * @return
     */
    public HashMap<String,Object> getNextUserInfo(String procInstId, String taskid, HashMap<String,Object> variables){
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
            //判断当前节点是否是会签节点
            try {
                ParallelMultiInstanceBehavior pmiBehavior = (ParallelMultiInstanceBehavior) userTask.getBehavior();
                String et = pmiBehavior.getCollectionVariable();
                et = et.replace(userTask.getId()+"_", "");
                variables = getUserInfoByTaskIdAndRuleName(userTask.getId(), et, variables, procInstId);
            }//继续执行
            catch (Exception ex){}
            try {
                SequentialMultiInstanceBehavior smiBehavior=(SequentialMultiInstanceBehavior)userTask.getBehavior();
                String et = smiBehavior.getCollectionVariable();
                et = et.replace(userTask.getId()+"_", "");
                variables = getUserInfoByTaskIdAndRuleName(userTask.getId(), et, variables, procInstId);
            }//继续执行
            catch (Exception ex){}
            //获取下一步到达的节点，也可能是网关
            List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
            //下一步必须是一个节点或网关，不支持不通过网关就到达多个节点的模式，因此只取第一个对象
            // todo:不通过网关就到达多个节点的模式待认证
            FlowElement targetFlowElement = outgoingFlows.get(0).getTargetFlowElement();
            //判断下一步是任务节点还是网关
            variables=this.getNextUsers(variables,targetFlowElement,procInstId);
            //返回接收人
            return variables;
        }
        catch(Exception ex) {
            return variables;
        }
    }

    /**
     * 获取下一节点处理人信息
     * @param variables
     * @param targetFlowElement
     * @param procInstId
     * @return
     */
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            ,String procInstId){

        //分支条件
        if(targetFlowElement instanceof ExclusiveGateway) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("ExclusiveGateway");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
        //并发
        else if(targetFlowElement instanceof ParallelGateway){
            SequenceFlowStrategy service=sequenceFlowContext.getService("ParallelGateway");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
        //满足条件的分支
        else if(targetFlowElement instanceof InclusiveGateway){
            SequenceFlowStrategy service=sequenceFlowContext.getService("InclusiveGateway");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
        //如果是结束
        else if(targetFlowElement instanceof EndEvent){
            SequenceFlowStrategy service=sequenceFlowContext.getService("EndEvent");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
        //如果是子流程
        else if (targetFlowElement instanceof SubProcess) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("SubProcess");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
        //如果是触发任务
        else if (targetFlowElement instanceof ReceiveTask) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("ReceiveTask");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
        //如果是调用活动
        else if (targetFlowElement instanceof CallActivity) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("CallActivity");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
        //用户任务
        else{
            SequenceFlowStrategy service=sequenceFlowContext.getService("UserTask");
            return service.getNextUsers(variables,targetFlowElement,procInstId);
        }
    }
    /**
     * 根据设置的规则，查找节点处理人
     * @param taskid
     * @param ruleName
     * @param variables
     * @param procInstId
     * @return
     */
    public HashMap<String,Object> getUserInfoByTaskIdAndRuleName(String taskid,String ruleName,
                                                HashMap<String,Object> variables,String procInstId){
        UserByTaskRuleStrategy userByTaskRuleService=userByTaskRuleContext.getService(ruleName);
        variables=userByTaskRuleService.getUserByTaskRule(taskid,ruleName,variables,procInstId);
        return variables;
    }
}
