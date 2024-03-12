package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ProcessTaskParams {
    /**
     * 流程定义key
     */
    private String processKey;
    /**
     * 流程定义名称
     */
    private String processName;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 业务id
     */
    private String businessKey;
    /**
     * 审核意见
     */
    private String msg;
    /**
     * 操作id（提交、驳回等）
     */
    private Integer actionId;
    /**
     * 操作名称
     */
    private String actionName;
    /**
     * 审核附件名称
     */
    private String msgFiles;
    /**
     * 审核附件下载地址
     */
    private String filePath;
    /**
     * 其他参数
     */
    private HashMap<String,Object> variables;
    /**
     * 知会人员集合
     */
    private List<CCData> ccData;

    private String tenantId;

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
