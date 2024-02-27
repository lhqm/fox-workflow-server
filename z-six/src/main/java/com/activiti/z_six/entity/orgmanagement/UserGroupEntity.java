package com.activiti.z_six.entity.orgmanagement;

import lombok.Data;

@Data
public class UserGroupEntity {
    /**
     * 主键
     */
    private String id;
    /**
     * 用户登录名
     */
    private String username;
    /**
     * 分组id
     */
    private String groupid;

}
