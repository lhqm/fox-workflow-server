package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class CopyForEntity {
    /**
     * 主键
     */
    private String mypk;
    /**
     * 节点key
     */
    private String task_def_key;
    /**
     * 流程实例id
     */
    private String proc_inst_id;
    /**
     * 目标id
     */
    private String targetId;
    /**
     * 抄送规则  手动or自动
     */
    private String ccWay;
    /**
     * 抄送范围规则
     */
    private String autoCCWay;
}
