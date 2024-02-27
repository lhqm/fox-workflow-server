package com.activiti.z_six.operLog.entity.base;

import com.activiti.z_six.common.Constants;
import org.springframework.data.annotation.Version;

import java.util.Optional;

public class BaseAllLongByEntity extends BaseLongByEntity{
    private String delFlag;
    @Version
    private Integer version;

    public BaseAllLongByEntity() {
    }

    public void preInsert(Long createUser) {
        super.preInsert(createUser);
        this.delFlag = "0";
        this.version = Constants.ONE;
    }

    public void preInsert() {
        super.preInsert();
        this.delFlag = "0";
        this.version = Constants.ONE;
    }

    public void preDelete(Long deleteUser) {
        super.preDelete(deleteUser);
        this.delFlag = "1";
    }

    public void preDelete() {
        super.preDelete();
        this.delFlag = "1";
    }

    public void preUpdate(Long updateUser) {
        super.preUpdate(updateUser);
        this.version = (Integer) Optional.ofNullable(this.version).map((var) -> {
            //var + 1;
            return var+1;
        }).orElse(Constants.ONE);
    }

    public void preUpdate() {
        super.preUpdate();
        this.version = (Integer)Optional.ofNullable(this.version).map((var) -> {
            return var+1;
        }).orElse(Constants.ONE);
    }

    public String getDelFlag() {
        return this.delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}

