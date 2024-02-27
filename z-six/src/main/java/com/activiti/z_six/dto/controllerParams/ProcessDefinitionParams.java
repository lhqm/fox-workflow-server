package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

import java.util.List;

@Data
public class ProcessDefinitionParams {
    /**
     * 流程id
     */
    private String id;
    /**
     * 流程编号
     */
    private String key;
    /**
     * 流程名称
     */
    private String name;
    /**
     * 流程类别
     */
    private String sortid;
    /**
     * 表单类型
     */
    private String form_type;
    /**
     * 表单JSON
     */
    private String formMap;
    /**
     * 表单地址
     */
    private String form_url;
    /**
     * 标题生成规则
     */
    private String titleModel;
    /**
     * 节点属性
     */
    private List<TaskDefinitionParams> userTasks;

}

