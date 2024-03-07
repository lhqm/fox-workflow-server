package com.activiti.z_six.strategy.flowConvert.impl;

import com.activiti.z_six.entity.tenant.FlowProcess;
import com.activiti.z_six.strategy.flowConvert.FlowElementStrategy;
import com.activiti.z_six.util.StringUtils;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;

/**
 * 中间流程路径节点
 */
public class SequenceFlowElementStrategy implements FlowElementStrategy {
    @Override
    public FlowProcess execute(FlowElement flowElement) {
        SequenceFlow element = (SequenceFlow) flowElement;
        return new FlowProcess(element.getId(), element.getSourceRef(), element.getTargetRef(),
                element.getName(), StringUtils.getClassNameByFullClassName(flowElement.getClass().getName()),null);
    }
}
