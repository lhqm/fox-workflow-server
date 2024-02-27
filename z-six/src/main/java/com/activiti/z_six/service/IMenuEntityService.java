package com.activiti.z_six.service;

import com.activiti.z_six.dto.orgDto.SysRoleMenu;
import com.activiti.z_six.entity.menu.MenuEntity;
import com.activiti.z_six.entity.menu.UserMenuEntity;

import java.util.List;

public interface IMenuEntityService {
    /**
     * 获取菜单列表
     * @param menuName
     * @param status
     * @return
     */
    List<MenuEntity> getMenuList(String menuName,String status);
    /**
     * 获取人员菜单
     * @return
     */
    List<UserMenuEntity> getUserMenuList();
    /**
     * 获取角色菜单
     * @param id
     * @return
     */
    List<SysRoleMenu> getRoleMenus(String id);

    /**
     * 更新菜单
     * @param menuEntity
     * @return
     */
    int updateMenu(MenuEntity menuEntity);

    /**
     * 增加菜单
     * @param menuEntity
     * @return
     */
    int addMenu(MenuEntity menuEntity);

    /**
     * 删除菜单
     * @param menuEntity
     * @return
     */
    int deleteMenu(MenuEntity menuEntity);
}
