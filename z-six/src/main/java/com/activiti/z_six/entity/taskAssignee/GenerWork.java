package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class GenerWork {
    /**
     * id
     */
    private String id;
    /**
     * 标题
     */
    private String title;
    /**
     * 流程key
     */
    private String processkey;
    /**
     * 流程实例id
     */
    private String proce_inst_id;
    /**
     * 节点编号
     */
    private String task_def_key;
    /**
     * 执行id
     */
    private String excution_id;
    /**
     * 创建时间
     */
    private String createtime;
    /**
     * 发起人
     */
    private String starter;
    /**
     * 绑定的表单json
     */
    private String map_json;
    /**
     * 绑定的表单类型
     */
    private String form_type;
    /**
     * 绑定的表单地址
     */
    private String form_url;
    /**
     * 数据json
     */
    private String data_json;
    /**
     * 任务id
     */
    private String taskid;
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 节点名称
     */
    private String taskName;
    /**
     * 结束时间
     */
    private String endtime;
    /**
     * 流程状态
     */
    private String act_type;

}
