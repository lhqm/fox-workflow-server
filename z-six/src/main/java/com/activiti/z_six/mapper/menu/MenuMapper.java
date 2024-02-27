package com.activiti.z_six.mapper.menu;

import com.activiti.z_six.dto.orgDto.SysRoleMenu;
import com.activiti.z_six.entity.menu.MenuEntity;
import com.activiti.z_six.entity.menu.UserMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MenuMapper {
    /**
     * 获取菜单详情
     * @param menuId
     * @return
     */
    MenuEntity getMenu(String menuId);
    /**
     * 获取菜单列表
     * @param menuName
     * @param status
     * @return
     */
    List<MenuEntity> getMenuList(@Param("menuName") String menuName,
                                 @Param("status") String status);

    /**
     * 获取人员菜单
     * @param userId
     * @return
     */
    List<UserMenuEntity> getUserMenuList(String userId);

    /**
     * 获取角色菜单
     * @param id
     * @return
     */
    List<SysRoleMenu> getRoleMenus(String id);

    /**
     * 增加菜单
     * @param menuEntity
     * @return
     */
    int addMenu(MenuEntity menuEntity);

    /**
     * 修改菜单
     * @param menuEntity
     * @return
     */
    int updayeMenu(MenuEntity menuEntity);

    /**
     * 删除菜单
     * @param meunId
     * @return
     */
    int deleteMenu(String meunId);
}
