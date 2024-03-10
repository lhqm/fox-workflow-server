package com.activiti.z_six.tenant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowMessage {
//    流程实例ID
    private String processInstanceId;
//    流程状态转换ID
    private String statusChangeId;
//    流程状态转换标识
    private String statusChangeText;
//    流程转换信息（审批的时候，相关审批人填了什么）
    private String processMessage;
//    源流转节点
    private String sourceTaskId;
//    目标流转节点
    private String targetTaskId;
//    审批时间
    private long processTime;
//    生成式随机确认码
    private String ackCode;
}
