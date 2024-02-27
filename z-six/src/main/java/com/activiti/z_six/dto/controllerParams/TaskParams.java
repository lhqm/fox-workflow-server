package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

@Data
public class TaskParams {
    /**
     * 实例id
     */
    private String proce_inst_id;
    /**
     * 运行id
     */
    private String taskid;
}
