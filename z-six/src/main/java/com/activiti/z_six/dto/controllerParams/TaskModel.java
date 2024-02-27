package com.activiti.z_six.dto.controllerParams;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class TaskModel {
    /**
     * 是否退回
     */
    private String returnWay;
    /**
     * 退回方式
     */
    private String runWay;
    /**
     * 是否移交
     */
    private Integer transfer;
    /**
     * 是否加签
     */
    private Integer countersign;
    /**
     * 是否跳转
     */
    private String jumpWay;
    /**
     * 是否可结束
     */
    private Integer endTask;
    /**
     * 是否拒绝
     */
    private Integer refuse;
    /**
     * 是否抄送
     */
    private String ccWay;
    /**
     * 抄送执行方式
     */
    private String autoCCWay;
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
}
