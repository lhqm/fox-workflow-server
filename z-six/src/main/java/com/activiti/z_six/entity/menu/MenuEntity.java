package com.activiti.z_six.entity.menu;

import com.activiti.z_six.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class MenuEntity extends BaseEntity implements Serializable {
    /** 菜单ID */
    private String menuId;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单名称 */
    private String parentName;

    /** 父菜单ID */
    private String parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否为外链（1是 0否） */
    private String isFrame;

    /** 类型（M目录 C菜单） */
    private String menuType;

    /** 显示状态（1显示 0隐藏） */
    private String visible;

    /** 菜单状态（1显示 0隐藏） */
    private String status;

    /** 权限字符串 */
    private String perms;

    /** 菜单图标 */
    private String icon;
    /** 备注 */
    private String remark;

    /** 子菜单 */
    private List<MenuEntity> children = new ArrayList<MenuEntity>();
}
