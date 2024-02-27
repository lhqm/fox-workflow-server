package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class OvProcessInstance {
    /**
     * 流程实例id
     */
    private String id_;
    /**
     * 实例名称
     */
    private String name_;
    /**
     * processKy
     */
    private String key_;
    /**
     * 版本号
     */
    private Integer version_;
    /**
     * 流程定义id
     */
    private String deployment_id_;
    /**
     * 流程分类
     */
    private String engine_version_;
}
