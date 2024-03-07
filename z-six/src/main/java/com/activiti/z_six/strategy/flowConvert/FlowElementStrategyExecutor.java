package com.activiti.z_six.strategy.flowConvert;

import com.activiti.z_six.entity.tenant.FlowProcess;
import com.activiti.z_six.strategy.flowConvert.impl.SequenceFlowElementStrategy;
import com.activiti.z_six.strategy.flowConvert.impl.SubProcessFlowElementStrategy;
import com.activiti.z_six.util.StringUtils;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;

import java.util.HashMap;
import java.util.Map;

public class FlowElementStrategyExecutor {
    private final Map<Class<?>, FlowElementStrategy> strategies;

    public FlowElementStrategyExecutor() {
        this.strategies = new HashMap<>();
        //TODO: 注册所有不可忽略的策略
//        流转流程元素（需要处理前后节点的组织关系）
        strategies.put(SequenceFlow.class,new SequenceFlowElementStrategy());
//        子流程元素（需要处理子流程的构造逻辑）
        strategies.put(SubProcess.class,new SubProcessFlowElementStrategy());
    }

    public FlowProcess executeStrategy(FlowElement flowElement) {
//        根据流程类型，选择执行对应的操作去处理元素节点组织关系
        Class<?> flowElementClass = flowElement.getClass();
        FlowElementStrategy strategy = strategies.get(flowElementClass);
//        匹配到策略，执行对应的处理
        if (strategy != null) {
            return strategy.execute(flowElement);
        } else {
            // 如果没有匹配的策略，返回未识别实例类型（也可能是下述操作已经足够满足获取逻辑了）
            return new FlowProcess(flowElement.getId(),null,null,flowElement.getName(), StringUtils.getClassNameByFullClassName(flowElement.getClass().getName()),null);
        }
    }
}