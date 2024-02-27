package com.activiti.z_six.strategy.sequenceFlow;
import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import org.activiti.bpmn.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 子流程
 */
@Component
public class SubProcessStrategy implements SequenceFlowStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            , String procInstId){
        //获取子流程对象
        Collection<FlowElement> subProcess=((SubProcess) targetFlowElement).getFlowElements();
        for (FlowElement fl : subProcess) {
            //判断是否是子流程开始节点
            if (fl instanceof StartEvent) {
                List<SequenceFlow> subOutgoingFlows =((StartEvent) fl).getOutgoingFlows();
                //取子流程中开始节点后面的第一个usertask
                UserTask subTask = (UserTask) subOutgoingFlows.get(0).getTargetFlowElement();
                variables=sequenceFlowManager.getVariables(subTask,variables,procInstId);
            }
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
    public List<UserTask> getNextTasks(HashMap<String,Object> variables, FlowElement targetFlowElement) {
        List<UserTask> userTaskList = new ArrayList<>();
        //获取子流程对象
        Collection<FlowElement> subProcess=((SubProcess) targetFlowElement).getFlowElements();
        for (FlowElement fl : subProcess) {
            //判断是否是子流程开始节点
            if (fl instanceof StartEvent) {
                List<SequenceFlow> subOutgoingFlows =((StartEvent) fl).getOutgoingFlows();
                //取子流程中开始节点后面的第一个usertask
                UserTask subTask = (UserTask) subOutgoingFlows.get(0).getTargetFlowElement();
                userTaskList.add(subTask);
            }
        }
        return userTaskList;
    }
    @Override
    public String getType(){
        return "SubProcess";
    }
}
