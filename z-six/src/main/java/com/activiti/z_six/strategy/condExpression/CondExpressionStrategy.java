package com.activiti.z_six.strategy.condExpression;

import com.activiti.z_six.entity.process.FlowCond;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public interface CondExpressionStrategy {
    /**
     * 解析表达式
     * @param sequenceFlow
     * @param flowCond
     * @return
     */
    HashMap<String,Object> analysis(HashMap<String,Object> variables,
                                    SequenceFlow sequenceFlow,
                                    FlowElement targetFlowElement,
                                    FlowCond flowCond,String procInstId);
    /**
     * 获取服务类型
     * @return
     */
    String getType();
}
