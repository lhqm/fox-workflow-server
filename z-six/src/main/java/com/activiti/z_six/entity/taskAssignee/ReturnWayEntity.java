package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class ReturnWayEntity {
    /**
     * 主键id
     */
    private String id;
    /**
     * 节点编号
     */
    private String task_def_id;
    /**
     * 退回方式
     */
    private String returnWay;
    /**
     * 退回后发送方式
     */
    private String runWay;
}