package com.activiti.z_six.strategy.sequenceFlow;

import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 同时发送给多个任务节点，并发
 */
@Component
public class ParallelGatewayStrategy implements SequenceFlowStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            , String procInstId){
        //获取并行网关分支对象
        List<SequenceFlow> parallelGateway=((ParallelGateway) targetFlowElement).getOutgoingFlows();
        for(SequenceFlow sf:parallelGateway){
            //转换成用户任务对象
            UserTask parallelTask=(UserTask)sf.getTargetFlowElement();
            variables=sequenceFlowManager.getVariables(parallelTask,variables,procInstId);
        }
        return variables;
    }
    /**
     * 获取下一步节点
     * @param variables
     * @param targetFlowElement
     * @return
     */
    @Override
    public List<UserTask> getNextTasks(HashMap<String,Object> variables, FlowElement targetFlowElement){
        List<UserTask> userTaskList=new ArrayList<>();
        //获取并行网关分支对象
        List<SequenceFlow> parallelGateway=((ParallelGateway) targetFlowElement).getOutgoingFlows();
        for(SequenceFlow sf:parallelGateway){
            UserTask parallelTask=(UserTask)sf.getTargetFlowElement();
            userTaskList.add(parallelTask);
        }
        return userTaskList;
    }
    @Override
    public String getType(){
        return "ParallelGateway";
    }
}
