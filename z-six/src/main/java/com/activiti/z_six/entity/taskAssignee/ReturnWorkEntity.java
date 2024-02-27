package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class ReturnWorkEntity {
    /**
     * id
     */
    private String id;
    /**
     * 流程定义id
     */
    private String proc_def_id;
    /**
     * 流程实例id
     */
    private String proc_inst_id;
    /**
     * 任务id
     */
    private String taskid;
    /**
     * 执行节点id
     */
    private String return_act_id;
    /**
     * 执行节点名称
     */
    private String return_act_name;
    /**
     * 退回到节点id
     */
    private String returnTo_act_id;
    /**
     * 退回到节点名称
     */
    private String returnTo_act_name;
    /**
     * 退回执行人
     */
    private String returnUser;
    /**
     * 退回执行时间
     */
    private String return_time;
    /**
     * 退回原因
     */
    private String return_msg;
    /**
     * 退回参数
     */
    private String variables;
}
