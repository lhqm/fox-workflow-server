package com.activiti.z_six.entity.taskAssignee;

import javax.naming.InitialContext;

public class FlowElementAttrs {
    //主键id
    private String id;
    private String process_key;
    private String titleModel;
    //节点id
    private String task_def_key;
    //驳回规则
    private String returnWay;
    //驳回后，发送规则
    private String runWay;
    //跳转规则
    private String jumpWay;
    //抄送规则
    private String ccWay;
    //自动抄送规则
    private String autoCCWay;
    //移交是否启用
    private Integer transfer;
    //加签是否启用
    private Integer countersign;
    //结束是否启用
    private Integer endTask;
    //拒绝是否启用
    private Integer refuse;
    private String form_type;
    private String formMap;
    private String form_url;
    private String mapJson;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcess_key() {
        return process_key;
    }

    public void setProcess_key(String process_key) {
        this.process_key = process_key;
    }

    public String getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(String titleModel) {
        this.titleModel = titleModel;
    }

    public String getTask_def_key() {
        return task_def_key;
    }

    public void setTask_def_key(String task_def_key) {
        this.task_def_key = task_def_key;
    }

    public String getReturnWay() {
        return returnWay;
    }

    public void setReturnWay(String returnWay) {
        this.returnWay = returnWay;
    }

    public String getRunWay() {
        return runWay;
    }

    public void setRunWay(String runWay) {
        this.runWay = runWay;
    }

    public String getJumpWay() {
        return jumpWay;
    }

    public void setJumpWay(String jumpWay) {
        this.jumpWay = jumpWay;
    }

    public String getCcWay() {
        return ccWay;
    }

    public void setCcWay(String ccWay) {
        this.ccWay = ccWay;
    }

    public String getAutoCCWay() {
        return autoCCWay;
    }

    public void setAutoCCWay(String autoCCWay) {
        this.autoCCWay = autoCCWay;
    }

    public Integer getTransfer() {
        return transfer;
    }

    public void setTransfer(Integer transfer) {
        this.transfer = transfer;
    }

    public Integer getCountersign() {
        return countersign;
    }

    public void setCountersign(Integer countersign) {
        this.countersign = countersign;
    }

    public Integer getEndTask() {
        return endTask;
    }

    public void setEndTask(Integer endTask) {
        this.endTask = endTask;
    }

    public Integer getRefuse() {
        return refuse;
    }

    public void setRefuse(Integer refuse) {
        this.refuse = refuse;
    }

    public String getForm_type() {
        return form_type;
    }

    public void setForm_type(String form_type) {
        this.form_type = form_type;
    }

    public String getFormMap() {
        return formMap;
    }

    public void setFormMap(String formMap) {
        this.formMap = formMap;
    }

    public String getForm_url() {
        return form_url;
    }

    public void setForm_url(String form_url) {
        this.form_url = form_url;
    }

    public String getMapJson() {
        return mapJson;
    }

    public void setMapJson(String mapJson) {
        this.mapJson = mapJson;
    }
}
