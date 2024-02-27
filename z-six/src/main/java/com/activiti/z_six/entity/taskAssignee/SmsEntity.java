package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

@Data
public class SmsEntity {
    /**
     * 主键id
     */
    private String id;
    /**
     * 流程运行id
     */
    private String proce_inst_id;
    /**
     * 消息内容
     */
    private String msgCont;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 发送时间
     */
    private String rdt;
    /**
     * 发送人
     */
    private String sender;
    /**
     * 消息接收人
     */
    private String toUser;
    /**
     * 消息状态（已读、未读、删除）
     */
    private String state;
    /**
     * 消息类型
     */
    private String smsType;
    /**
     * 头像
     */
    private String avatar;

}
