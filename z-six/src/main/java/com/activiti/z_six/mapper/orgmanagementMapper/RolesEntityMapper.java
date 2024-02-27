package com.activiti.z_six.mapper.orgmanagementMapper;

import com.activiti.z_six.dto.orgDto.SysRoleDept;
import com.activiti.z_six.dto.orgDto.SysRoleMenu;
import com.activiti.z_six.entity.orgmanagement.RolesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@Repository
public interface RolesEntityMapper {
    /**
     * 获取角色列表
     * @return
     */
    List<RolesEntity> rolesList(@Param("name")String name);

    /**
     * 获取角色列表--分页
     * @param page
     * @param pagesize
     * @return
     */
    List<RolesEntity> rolesListPage(@Param("name")String name,@Param("page")Integer page,@Param("pagesize")Integer pagesize);

    /**
     * 获取人员所有的角色
     * @param username
     * @return
     */
    List<RolesEntity> rolesListByUser(String username);

    /**
     * 角色部门权限
     * @param id
     * @return
     */
    List<SysRoleDept> roleDeptTreeselect(String id);

    /**
     * 获取角色属性
     * @param id
     * @return
     */
    RolesEntity rolesEntity(String id);

    /**
     * 增加
     * @param rolesEntity
     * @return
     */
    int addRoles(RolesEntity rolesEntity);

    /**
     * 批量增加角色菜单
     * @param roleMenuList
     * @return
     */
    int batchRoleMenu(List<SysRoleMenu> roleMenuList);

    /**
     * 批量增加数据权限
     * @param sysRoleDepts
     * @return
     */
    int batchRoleDept(List<SysRoleDept> sysRoleDepts);
    /**
     * 修改
     * @param rolesEntity
     * @return
     */
    int updateRoles(RolesEntity rolesEntity);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteRoles(String id);

    /**
     * 删除用户角色
     * @param username
     * @return
     */
    int deleteRoleUser(String username);

    /**
     * 删除角色菜单
     * @param id
     * @return
     */
    int deleteRoleMenu(String id);

    /**
     * 删除数据权限
     * @param id
     * @return
     */
    int deleteDataScope(String id);
}
