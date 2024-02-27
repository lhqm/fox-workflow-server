package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class TaskInstEntity {
    /**
     * id
     */
    private String id_;
    /**
     * 流程定义id
     */
    private String proc_def_id_;
    /**
     * 节点key
     */
    private String task_def_key_;
    /**
     * 流程实例id
     */
    private String proc_inst_id_;
    /**
     * 流程执行id
     */
    private String execution_id_;
    /**
     * 节点名称
     */
    private String name_;
    /**
     * 父节点id
     */
    private String parent_task_id_;
    /**
     * 流程备注
     */
    private String description_;
    /**
     * 拥有者
     */
    private String owner_;
    /**
     * 处理人
     */
    private String assignee_;
    /**
     * 开始时间
     */
    private String start_time_;
    /**
     * 任务拾取时间
     */
    private String claim_time_;
    /**
     * 任务完成时间
     */
    private String end_time_;
    /**
     * 用时
     */
    private Long duration_;
    //
    private String delete_reason_;
    /**
     * 优先级
     */
    private Integer priority_;
    private String due_date_;
    private String form_key_;
    /**
     * 类别
     */
    private String category_;
    private String tenant_id_;
    /**
     * 任务id
     */
    private String task_id_;
    private String act_id_;
    private String act_name_;
    private String act_type_;
}
