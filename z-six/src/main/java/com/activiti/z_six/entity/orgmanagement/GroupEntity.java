package com.activiti.z_six.entity.orgmanagement;

import java.util.List;

public class GroupEntity {
    /**
     * 分组id
     */
    private String id;
    /**
     * 分组名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 分组人员集合
     */
    private List<UserEntity> userEntities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }
}
