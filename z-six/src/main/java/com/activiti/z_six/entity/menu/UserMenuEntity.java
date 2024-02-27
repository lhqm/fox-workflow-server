package com.activiti.z_six.entity.menu;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserMenuEntity {
    /**
     * 菜单id
     */
    private String menuId;
    /**
     * 父节点id
     */
    private String parentId;
    /**
     * 标识
     */
    private String path;
    /**
     * 组件Name
     */
    private String name;
    /**
     * 菜单名称
     */
    private String title;
    /**
     * 是否显示 1=显示，0=隐藏
     */
    private Integer show;
    /** 是否为外链（1是 0否） */
    private String isFrame;
    /**
     * 图标
     */
    private String iconSvg;
    /**
     * 菜单地址
     */
    private String component;
    /** 路由参数 */
    private String query;
    /** 子菜单 */
    private List<UserMenuEntity> children = new ArrayList<UserMenuEntity>();
}
