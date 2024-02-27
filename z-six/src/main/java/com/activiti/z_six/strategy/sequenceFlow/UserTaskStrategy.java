package com.activiti.z_six.strategy.sequenceFlow;

import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户任务
 */
@Component
public class UserTaskStrategy implements SequenceFlowStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            , String procInstId){
        UserTask defUserTask=(UserTask) targetFlowElement;
        variables=sequenceFlowManager.getVariables(defUserTask,variables,procInstId);
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
        userTaskList.add((UserTask) targetFlowElement);
        return userTaskList;
    }
    @Override
    public String getType(){
        return "UserTask";
    }
}
