package com.activiti.z_six.strategy.userByTaskRule;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 查找关联人的策略
 */
//TODO：用这个接口实现找人逻辑
@Component
public interface UserByTaskRuleStrategy {
    /**
     * 获取节点处理人
     * @param taskid
     * @param ruleName
     * @param variables
     * @param procInstId
     * @return
     */
    HashMap<String,Object> getUserByTaskRule(String taskid,String ruleName
            ,HashMap<String,Object> variables,String procInstId);

    /**
     * 获取服务类型
     * @return
     */
    String getType();
}
