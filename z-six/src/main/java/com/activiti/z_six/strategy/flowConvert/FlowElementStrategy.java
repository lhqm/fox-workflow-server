package com.activiti.z_six.strategy.flowConvert;

import com.activiti.z_six.entity.tenant.FlowProcess;
import org.activiti.bpmn.model.FlowElement;

/**
 * 策略执行类接口
 */
public interface FlowElementStrategy {
    FlowProcess execute(FlowElement flowElement);
}