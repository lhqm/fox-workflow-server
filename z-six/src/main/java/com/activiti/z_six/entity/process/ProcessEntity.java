package com.activiti.z_six.entity.process;

public class ProcessEntity {
    //deploymentid
    private String deployment_id_;
    //流程分类
    private String engine_version_;
    //流程名称
    private String name_;

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getDeployment_id_() {
        return deployment_id_;
    }

    public void setDeployment_id_(String deployment_id_) {
        this.deployment_id_ = deployment_id_;
    }

    public String getEngine_version_() {
        return engine_version_;
    }

    public void setEngine_version_(String engine_version_) {
        this.engine_version_ = engine_version_;
    }
}
