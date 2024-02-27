package com.activiti.z_six.operLog.entity.base;

import java.util.Date;

public class BaseLongByEntity implements BaseLongBy, BaseLongEntity {
    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;

    public BaseLongByEntity() {
    }

    public void preInsert(Long createBy) {
        Date date = new Date();
        this.updateTime = date;
        this.createTime = date;
        this.createBy = createBy;
        this.updateBy = createBy;
    }

    public void preInsert() {
        Date date = new Date();
        this.updateTime = date;
        this.createTime = date;
        this.updateBy = this.createBy;
    }

    public void preDelete(Long deleteBy) {
        this.updateBy = deleteBy;
        this.updateTime = new Date();
    }

    public void preDelete() {
        this.updateTime = new Date();
    }

    public void preUpdate(Long updateBy) {
        this.updateTime = new Date();
        this.updateBy = updateBy;
    }

    public void preUpdate() {
        this.updateTime = new Date();
    }

    public Long getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

