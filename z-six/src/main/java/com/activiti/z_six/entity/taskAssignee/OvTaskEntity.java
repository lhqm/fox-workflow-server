package com.activiti.z_six.entity.taskAssignee;

public class OvTaskEntity {
    /**
     * 主键
     */
    private String id_;
    /**
     * 执行id
     */
    private String execution_id_;
    /**
     * processinstid
     */
    private String proc_inst_id_;
    /**
     * 流程部署id
     */
    private String proc_def_id_;
    /**
     * 任务名称
     */
    private String name_;
    /**
     * 父流程任务id
     */
    private String parent_task_id;
    /**
     * 说明
     */
    private String description_;
    /**
     * 节点id
     */
    private String task_def_key_;
    /**
     * 执行节点
     */
    private String execution_task_def_key_;
    /**
     * 拥有者
     */
    private String owner_;
    /**
     * 执行人
     */
    private String assignee_;
    /**
     * 委托人
     */
    private String delegation_;
    /**
     * 优先级
     */
    private Integer priority_;
    /**
     * 创建时间
     */
    private String create_time_;
    /**
     * 到期时间
     */
    private String due_date_;
    /**
     * 类别
     */
    private String category_;
    /**
     * 是否暂停  0=是，1=否
     */
    private Integer suspension_state_;
    /**
     * 租户id
     */
    private String tenant_id_;
    /**
     * 表单key，可以是一个约定值，也可以是一个连接
     */
    private String form_key_;
    /**
     * 任务提醒时间
     */
    private String claim_time_;

    public String getId_() {
        return id_;
    }

    public void setId_(String id_) {
        this.id_ = id_;
    }

    public String getExecution_id_() {
        return execution_id_;
    }

    public void setExecution_id_(String execution_id_) {
        this.execution_id_ = execution_id_;
    }

    public String getProc_inst_id_() {
        return proc_inst_id_;
    }

    public void setProc_inst_id_(String proc_inst_id_) {
        this.proc_inst_id_ = proc_inst_id_;
    }

    public String getProc_def_id_() {
        return proc_def_id_;
    }

    public void setProc_def_id_(String proc_def_id_) {
        this.proc_def_id_ = proc_def_id_;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getParent_task_id() {
        return parent_task_id;
    }

    public void setParent_task_id(String parent_task_id) {
        this.parent_task_id = parent_task_id;
    }

    public String getDescription_() {
        return description_;
    }

    public void setDescription_(String description_) {
        this.description_ = description_;
    }

    public String getTask_def_key_() {
        return task_def_key_;
    }

    public void setTask_def_key_(String task_def_key_) {
        this.task_def_key_ = task_def_key_;
    }

    public String getExecution_task_def_key_() {
        return execution_task_def_key_;
    }

    public void setExecution_task_def_key_(String execution_task_def_key_) {
        this.execution_task_def_key_ = execution_task_def_key_;
    }

    public String getOwner_() {
        return owner_;
    }

    public void setOwner_(String owner_) {
        this.owner_ = owner_;
    }

    public String getAssignee_() {
        return assignee_;
    }

    public void setAssignee_(String assignee_) {
        this.assignee_ = assignee_;
    }

    public String getDelegation_() {
        return delegation_;
    }

    public void setDelegation_(String delegation_) {
        this.delegation_ = delegation_;
    }

    public Integer getPriority_() {
        return priority_;
    }

    public void setPriority_(Integer priority_) {
        this.priority_ = priority_;
    }

    public String getCreate_time_() {
        return create_time_;
    }

    public void setCreate_time_(String create_time_) {
        this.create_time_ = create_time_;
    }

    public String getDue_date_() {
        return due_date_;
    }

    public void setDue_date_(String due_date_) {
        this.due_date_ = due_date_;
    }

    public String getCategory_() {
        return category_;
    }

    public void setCategory_(String category_) {
        this.category_ = category_;
    }

    public Integer getSuspension_state_() {
        return suspension_state_;
    }

    public void setSuspension_state_(Integer suspension_state_) {
        this.suspension_state_ = suspension_state_;
    }

    public String getTenant_id_() {
        return tenant_id_;
    }

    public void setTenant_id_(String tenant_id_) {
        this.tenant_id_ = tenant_id_;
    }

    public String getForm_key_() {
        return form_key_;
    }

    public void setForm_key_(String form_key_) {
        this.form_key_ = form_key_;
    }

    public String getClaim_time_() {
        return claim_time_;
    }

    public void setClaim_time_(String claim_time_) {
        this.claim_time_ = claim_time_;
    }
}
