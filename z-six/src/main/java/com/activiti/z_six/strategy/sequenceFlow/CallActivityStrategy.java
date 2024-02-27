package com.activiti.z_six.strategy.sequenceFlow;

import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 调用任务
 */
@Component
public class CallActivityStrategy implements SequenceFlowStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            , String procInstId){
        // todo:1.调用子流程（数据同步、父流程进入下一步？父流程暂停？子流程结束后父流程进入下一步？）
        // todo:2.调用webapi
        // todo:3.调用sql
        CallActivity callActivity = (CallActivity) targetFlowElement;
        List<SequenceFlow> callActivityOutgoingFlows = callActivity.getOutgoingFlows();
        UserTask callActivityTask = (UserTask) callActivityOutgoingFlows.get(0).getTargetFlowElement();
        variables=sequenceFlowManager.getVariables(callActivityTask,variables,procInstId);
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
        CallActivity callActivity = (CallActivity) targetFlowElement;
        List<SequenceFlow> callActivityOutgoingFlows = callActivity.getOutgoingFlows();
        UserTask callActivityTask = (UserTask) callActivityOutgoingFlows.get(0).getTargetFlowElement();

        UserTask callActivityUserTask=new UserTask();
        callActivityUserTask.setId("callActivityTask");
        callActivityUserTask.setName("调用任务节点");
        userTaskList.add(callActivityUserTask);

        userTaskList.add(callActivityTask);
        return userTaskList;
    }
    @Override
    public String getType(){
        return "CallActivity";
    }
}
