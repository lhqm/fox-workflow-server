package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class ManuallySelectEntity {
    private String id;
    private String task_def_id;
    private String proc_inst_id;
    private String username;
}
