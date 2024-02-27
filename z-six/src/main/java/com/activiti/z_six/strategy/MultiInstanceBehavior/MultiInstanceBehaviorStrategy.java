package com.activiti.z_six.strategy.MultiInstanceBehavior;

import org.activiti.bpmn.model.UserTask;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 处理人规则判断
 */
@Component
public interface MultiInstanceBehaviorStrategy {
    /**
     * 获取处理人
     * @return
     */
    HashMap<String,Object> getMulitUsers(UserTask userTask,HashMap<String,Object> variables,
                                         String procInstId);
    /**
     * 获取服务类型
     * @return
     */
    String getType();
}
