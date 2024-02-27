package com.activiti.z_six.strategy.MultiInstanceBehavior;

import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 并行多重
 */
@Component
public class ParallelMultiInstanceStrategy implements MultiInstanceBehaviorStrategy{
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
        ParallelMultiInstanceBehavior parallelMultiInstanceBehavior=(ParallelMultiInstanceBehavior)userTask.getBehavior();
        //获取多实例处理规则
        String expressionText=parallelMultiInstanceBehavior.getCollectionVariable();
        expressionText=expressionText.replace(userTask.getId()+"_","");
        //获取处理人
        variables=sequenceFlowManager.getUserInfoByTaskIdAndRuleName(userTask.getId(),
                expressionText,variables,procInstId);
        return variables;
    }
    /**
     * 服务标识
     * @return
     */
    @Override
    public String getType(){
        return "ParallelMultiInstanceBehavior";
    }
}
