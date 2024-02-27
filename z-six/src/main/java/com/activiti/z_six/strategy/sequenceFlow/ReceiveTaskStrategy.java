package com.activiti.z_six.strategy.sequenceFlow;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 等待任务
 */
@Component
public class ReceiveTaskStrategy implements SequenceFlowStrategy{
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            , String procInstId){
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
        UserTask receiveTask=new UserTask();
        receiveTask.setId("receiveTask");
        receiveTask.setName("等待任务节点");
        userTaskList.add(receiveTask);
        return userTaskList;
    }
    @Override
    public String getType(){
        return "ReceiveTask";
    }
}
