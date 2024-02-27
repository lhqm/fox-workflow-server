package com.activiti.z_six.dto.orgDto;

import lombok.Data;

@Data
public class SysRoleDto {
    /** 角色ID */
    private String id;
    /** 角色名称 */
    private String name;
    /** 菜单组 */
    private String[] menuIds;
    /**
     * 权限范围
     */
    private String dataScope;
    /**
     * 数据权限
     */
    private String[] depts;
}
