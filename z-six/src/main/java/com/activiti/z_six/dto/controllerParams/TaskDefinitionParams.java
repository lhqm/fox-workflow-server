package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

import java.util.List;

@Data
public class TaskDefinitionParams {
    /**
     * 节点id
     */
    private String id;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点控制
     */
    private TaskModel formModel;
}
