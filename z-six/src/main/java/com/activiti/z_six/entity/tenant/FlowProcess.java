package com.activiti.z_six.entity.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowProcess {
    /**
     * 当前流程元素ID
     */
    private String flowId;
    /**
     * 前一个流程元素ID
     */
    private String prev;
    /**
     * 后一个流程元素ID
     */
    private String next;
    /**
     * 流程元素名称
     */
    private String flowName;
    /**
     * 流程元素类型
     */
    private String flowType;
    /**
     * 流程节点所指向的子流程（需要注意，有这个标记的，一定是子流程的外围表达框图）
     */
    private Collection<FlowProcess> subProcess;

}
