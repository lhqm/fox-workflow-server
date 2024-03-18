package com.activiti.z_six.tenant.service;

import com.activiti.z_six.tenant.model.api.FlowMessage;

import java.util.List;

/**
 * 租户端流程状态迁移通知
 */
public interface WorkFlowMessageContext {
    /**
     * 状态通知存库
     * @param flowMessage 通知数据
     * @param tenant 租户
     */
    void StorageMessageByTenant(FlowMessage flowMessage,String tenant);

    /**
     * 获取流程信息
     * @return 从状态通知库里返回存库数据
     * @param autoMode 是否开启自动确认模式
     */
    List<FlowMessage> getFlowMessage(Boolean autoMode,String tenant);

    /**
     * 响应并删除相关存库数据
     * @param askList 确认列表
     * @return 确认数
     */
    Long receiveFlowMessageList(List<String> askList,String tenant);

    /**
     * 响应并删除相关存库数据
     * @param askCode 确认列表
     * @return 确认数
     */
    Long receiveFlowMessage(String askCode,String tenant);

}
