package com.activiti.z_six.templete;

import com.activiti.z_six.strategy.sequenceFlow.SequenceFlowContext;
import com.activiti.z_six.strategy.sequenceFlow.SequenceFlowStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindBpmModel {
    @Autowired
    private SequenceFlowContext sequenceFlowContext;

    /**
     * 获取下一节点处理人信息
     * @param variables
     * @param targetFlowElement
     * @return
     */
    public List<UserTask> getNextTasks(HashMap<String,Object> variables, FlowElement targetFlowElement){

        //分支条件
        if(targetFlowElement instanceof ExclusiveGateway) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("ExclusiveGateway");
            return service.getNextTasks(variables,targetFlowElement);
        }
        //并发
        else if(targetFlowElement instanceof ParallelGateway){
            SequenceFlowStrategy service=sequenceFlowContext.getService("ParallelGateway");
            return service.getNextTasks(variables,targetFlowElement);
        }
        //满足条件的分支
        else if(targetFlowElement instanceof InclusiveGateway){
            SequenceFlowStrategy service=sequenceFlowContext.getService("InclusiveGateway");
            return service.getNextTasks(variables,targetFlowElement);
        }
        //如果是结束
        else if(targetFlowElement instanceof EndEvent){
            SequenceFlowStrategy service=sequenceFlowContext.getService("EndEvent");
            return service.getNextTasks(variables,targetFlowElement);
        }
        //如果是子流程
        else if (targetFlowElement instanceof SubProcess) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("SubProcess");
            return service.getNextTasks(variables,targetFlowElement);
        }
        //如果是触发任务
        else if (targetFlowElement instanceof ReceiveTask) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("ReceiveTask");
            return service.getNextTasks(variables,targetFlowElement);
        }
        //如果是调用活动
        else if (targetFlowElement instanceof CallActivity) {
            SequenceFlowStrategy service=sequenceFlowContext.getService("CallActivity");
            return service.getNextTasks(variables,targetFlowElement);
        }
        //用户任务
        else{
            SequenceFlowStrategy service=sequenceFlowContext.getService("UserTask");
            return service.getNextTasks(variables,targetFlowElement);
        }
    }
}
