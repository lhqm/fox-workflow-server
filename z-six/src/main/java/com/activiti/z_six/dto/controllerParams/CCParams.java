package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

import java.util.List;

@Data
public class CCParams {
    /**
     * 任务节点key
     */
    private String task_key;
    /**
     * 流程实例id
     */
    private String proc_inst_id;
    /**
     * 抄送规则
     */
    private String ccWay;
    /**
     * 抄送范围规则
     */
    private String autoCCWay;
    /**
     * 抄送人员（岗位、角色....等数据）
     */
    private List<CCData> data;

    @Data
    public static class CCData{
        /**
         * id
         */
        private String id;
        /**
         * 用户名
         */
        private String username;
        /**
         * 名称
         */
        private String name;
    }
}

