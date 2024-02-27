package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class ApprovalTrack {
    /**
     * 主键
     */
    private String uuid;
    /**
     * 节点id
     */
    private String userTaskId;
    /**
     * 节点名称
     */
    private String userTaskName;
    /**
     * 审核人帐号
     */
    private String user;
    /**
     * 审核人姓名
     */
    private String userName;
    /**
     * 审核日期
     */
    private String rdt;
    /**
     * 审核意见
     */
    private String msg;
    /**
     * 审核附件
     */
    private String msgFiles;
    /**
     * 操作类型（发送、驳回、移交等等）
     */
    private Integer actionType;
    /**
     * 操作类型名称
     */
    private String actionName;
    /**
     * 流程实例id
     */
    private String process_ints_id;
    /**
     * 审核附件位置
     */
    private String filePath;
}

