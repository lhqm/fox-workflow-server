package com.activiti.z_six.strategy.sequenceFlow;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public interface SequenceFlowStrategy {
    /**
     * 获取下一步节点处理人
     * @param variables
     * @param targetFlowElement
     * @return
     */
    HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            ,String procInstId);

    /**
     * 获取下一步节点
     * @param variables
     * @param targetFlowElement
     * @return
     */
    List<UserTask> getNextTasks(HashMap<String,Object> variables, FlowElement targetFlowElement);

    /**
     * 获取服务类型
     * @return
     */
    String getType();
}
