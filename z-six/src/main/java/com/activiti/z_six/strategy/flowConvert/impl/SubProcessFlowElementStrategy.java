package com.activiti.z_six.strategy.flowConvert.impl;

import com.activiti.z_six.entity.tenant.FlowProcess;
import com.activiti.z_six.strategy.flowConvert.FlowElementStrategy;
import com.activiti.z_six.util.StringUtils;
import com.activiti.z_six.util.flow.FlowElementUtil;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SubProcess;

public class SubProcessFlowElementStrategy implements FlowElementStrategy {
    @Override
    public FlowProcess execute(FlowElement flowElement) {
//        对于
        SubProcess element = (SubProcess) flowElement;
        return new FlowProcess(element.getId(),null,null,
                flowElement.getName(),
                StringUtils.getClassNameByFullClassName(flowElement.getClass().getName()),
                FlowElementUtil.getFlowDefinitionMap(element.getFlowElements()));
    }
}
