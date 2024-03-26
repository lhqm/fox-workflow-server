package com.activiti.z_six.dto;

import lombok.Data;

@Data
public class SendActionDto {
    /**
     * 状态
     */
    private int state;
    /**
     * 状态信息
     */
    private String msg;
    /**
     * 节点ID
     */
    private Object task_key;
    /**
     * 当前节点id，避免与跳转重复
     */
    private String curTask_key;
    /**
     * 节点名称
     */
    private String taskName;
    /**
     * 流程实例id
     */
    private String proc_inst_id;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 是否跳转
     */
    private String jumpWay;
    /**
     * 是否抄送
     */
    private String ccWay;
    /**
     * 抄送规则
     */
    private String autoCCWay;
    /**
     * 路程是否已结束
     */
    private Boolean endTask;
    /**
     * 扩展参数
     */
    private String params;
    private String processInstanceId;
}
