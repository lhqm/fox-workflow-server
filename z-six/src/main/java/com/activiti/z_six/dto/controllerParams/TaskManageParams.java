package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

import java.util.List;

@Data
public class TaskManageParams {
    /**
     * 流程实例id
     */
    private String proc_inst_id;
    /**
     * 任务id
     */
    private String taskid;
    /**
     * 移交信息
     */
    private String msg;
    /**
     * 接收人
     */
    private String toUser;
    /**
     * 流程信息
     */
    private List<TaskParams> taskParams;
}
