package com.activiti.z_six.strategy.manager;

import com.activiti.z_six.strategy.MultiInstanceBehavior.MultiInstanceBehaviorContext;
import com.activiti.z_six.strategy.MultiInstanceBehavior.MultiInstanceBehaviorStrategy;
import com.activiti.z_six.strategy.userByTaskRule.UserByTaskRuleContext;
import com.activiti.z_six.strategy.userByTaskRule.UserByTaskRuleStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceFlowManager {
    @Autowired
    private UserByTaskRuleContext userByTaskRuleContext;
    @Autowired
    private MultiInstanceBehaviorContext multiInstanceBehaviorContext;

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

    /**
     * 获取节点设置的规则
     * @param task_def_key
     * @param candidateUsersKey
     * @return
     */
    public String getRuleName(String task_def_key,String candidateUsersKey){
        String ruleName=candidateUsersKey.replace("$","");
        ruleName=ruleName.replace("{","");
        ruleName=ruleName.replace("}","");
        ruleName=ruleName.replace(task_def_key+"_","");
        return ruleName;
    }

    /**
     * 根据处理人规则获取参数
     * todo:在这里去执行多重事件。每次多重事件读取后都需要执行找人逻辑。会签节点的数量限制和动态加人就可以在这里实现
     * @param userTask
     * @param variables
     * @param procInstId
     * @return
     */
    public HashMap<String,Object> getVariables(UserTask userTask, HashMap<String,Object> variables,
                                                String procInstId){
        //多实例处理规则，如果是 时序多重事件
        if(userTask.getBehavior() instanceof SequentialMultiInstanceBehavior){
            MultiInstanceBehaviorStrategy service=
                    multiInstanceBehaviorContext.getService("SequentialMultiInstanceBehavior");
            variables=service.getMulitUsers(userTask,variables,procInstId);
            return variables;
        } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
            MultiInstanceBehaviorStrategy service=
                    multiInstanceBehaviorContext.getService("ParallelMultiInstanceBehavior");
            variables=service.getMulitUsers(userTask,variables,procInstId);
            return variables;
        }
        else{
            MultiInstanceBehaviorStrategy service=
                    multiInstanceBehaviorContext.getService("NoMulitInstance");
            variables=service.getMulitUsers(userTask,variables,procInstId);
            return variables;
        }
    }
}
