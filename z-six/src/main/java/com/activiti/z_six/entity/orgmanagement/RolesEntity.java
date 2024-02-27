package com.activiti.z_six.entity.orgmanagement;

import lombok.Data;

@Data
public class RolesEntity {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private String data_scope;
}
