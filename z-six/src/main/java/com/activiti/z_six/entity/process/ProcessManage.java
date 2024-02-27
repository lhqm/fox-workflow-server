package com.activiti.z_six.entity.process;

import lombok.Data;

@Data
public class ProcessManage {
    /**
     * 流程实例id
     */
    private String proc_inst_id_;
    /**
     * 业务id
     */
    private String business_key_;
    /**
     * 流程名称
     */
    private String name_;
    /**
     * 开始时间
     */
    private String start_time_;
    /**
     * 发起人
     */
    private String start_user_id_;
    /**
     * 结束时间
     */
    private String end_time_;
    /**
     * 标题
     */
    private String title;
    /**
     * 业务数据json
     */
    private String map_json;
    /**
     * 数据json
     */
    private String data_json;
    /**
     * 发起人名称
     */
    private String startname;
    /**
     * 流程Key
     */
    private String key_;
    /**
     * 删除状态
     */
    private String delete_reason_;
}
