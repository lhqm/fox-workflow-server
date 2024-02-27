package com.activiti.z_six.strategy.MultiInstanceBehavior;

import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 一个人处理
 */
@Component
public class NoMulitInstanceStrategy implements MultiInstanceBehaviorStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;

    /**
     * 获取节点处理人
     * @param userTask
     * @param variables
     * @param procInstId
     * @return
     */
    @Override
    public HashMap<String,Object> getMulitUsers(UserTask userTask, HashMap<String,Object> variables,
                                                String procInstId){
        //获取处理人规则
        String candidateUsers=userTask.getCandidateUsers().get(0);
        String ruleName=sequenceFlowManager.getRuleName(userTask.getId(),candidateUsers);
        //获取处理人
        variables=sequenceFlowManager.getUserInfoByTaskIdAndRuleName(userTask.getId(),
                ruleName,variables,procInstId);
        return variables;
    }

    /**
     * 服务标识
     * @return
     */
    @Override
    public String getType(){
        return "NoMulitInstance";
    }
}
