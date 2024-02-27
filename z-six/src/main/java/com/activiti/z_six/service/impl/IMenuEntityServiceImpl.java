package com.activiti.z_six.service.impl;

import com.activiti.z_six.dto.orgDto.SysRoleMenu;
import com.activiti.z_six.entity.menu.MenuEntity;
import com.activiti.z_six.entity.menu.UserMenuEntity;
import com.activiti.z_six.mapper.menu.MenuMapper;
import com.activiti.z_six.service.IMenuEntityService;
import com.activiti.z_six.service.manager.CommManager;
import com.activiti.z_six.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IMenuEntityServiceImpl implements IMenuEntityService {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private CommManager commManager;

    /**
     * 获取菜单列表
     * @param menuName
     * @param status
     * @return
     */
    @Override
    public List<MenuEntity> getMenuList(String menuName, String status){
        return menuMapper.getMenuList(menuName,status);
    }
    /**
     * 获取人员菜单
     * @return
     */
    @Override
    public List<UserMenuEntity> getUserMenuList(){
        List<UserMenuEntity> userMenuEntities=menuMapper.getUserMenuList(commManager.getUserNo());
        if(userMenuEntities.size()==0){
            UserMenuEntity userMenuEntity=new UserMenuEntity();
            userMenuEntity.setMenuId("10");
            userMenuEntity.setName("index");
            userMenuEntity.setTitle("首页");
            userMenuEntity.setPath("index");
            userMenuEntity.setShow(1);
            userMenuEntity.setIconSvg("menu-home");
            userMenuEntity.setIsFrame("0");
            userMenuEntity.setParentId("0");
            userMenuEntities.add(userMenuEntity);
        }
        return userMenuEntities;
    }
    /**
     * 获取角色菜单
     * @param id
     * @return
     */
    @Override
    public List<SysRoleMenu> getRoleMenus(String id){
        return menuMapper.getRoleMenus(id);
    }
    /**
     * 更新菜单
     * @param menuEntity
     * @return
     */
    @Override
    public int updateMenu(MenuEntity menuEntity){
        MenuEntity menu=new MenuEntity();
        BeanUtils.copyProperties(menuEntity,menu);
        menu.setUpdateBy(commManager.getUserNo());
        menu.setUpdateTime(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
        return menuMapper.updayeMenu(menu);
    }

    /**
     * 增加菜单
     * @param menuEntity
     * @return
     */
    @Override
    public int addMenu(MenuEntity menuEntity){
        MenuEntity menu=new MenuEntity();
        BeanUtils.copyProperties(menuEntity,menu);
        menu.setMenuId(UUID.randomUUID().toString());
        menu.setCreateBy(commManager.getUserNo());
        menu.setCreateTime(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
        return menuMapper.addMenu(menu);
    }

    /**
     * 删除菜单
     * @param menuEntity
     * @return
     */
    @Override
    public int deleteMenu(MenuEntity menuEntity){

        return menuMapper.deleteMenu(menuEntity.getMenuId());
    }
}
