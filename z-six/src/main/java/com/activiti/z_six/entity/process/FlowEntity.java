package com.activiti.z_six.entity.process;

public class FlowEntity {
    /**
     * deploymentid
     */
    private String id_;
    /**
     * 流程名称
     */
    private String name_;
    /**
     * 流程分类
     */
    private String engine_version_;
    /**
     * 流程实例Key
     */
    private String processKey;

    public String getId_() {
        return id_;
    }

    public void setId_(String id_) {
        this.id_ = id_;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getEngine_version_() {
        return engine_version_;
    }

    public void setEngine_version_(String engine_version_) {
        this.engine_version_ = engine_version_;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }
}
