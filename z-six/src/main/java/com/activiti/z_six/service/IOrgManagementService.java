package com.activiti.z_six.service;

import com.activiti.z_six.common.BusinessException;
import com.activiti.z_six.dto.controllerParams.GroupUsersParams;
import com.activiti.z_six.dto.controllerParams.UserParams;
import com.activiti.z_six.dto.orgDto.SysRoleDept;
import com.activiti.z_six.dto.orgDto.SysRoleDto;
import com.activiti.z_six.dto.orgDto.SysRoleMenu;
import com.activiti.z_six.entity.orgmanagement.*;

import java.util.HashMap;
import java.util.List;

public interface IOrgManagementService {
    /**
     * 获取公司列表
     * @return
     */
    List<CompanyEntity> getCompanyList();

    /**
     * 增加公司
     * @param name
     * @param parentid
     * @return
     */
    String addCompany(String name, Integer parentid);

    /**
     * 修改公司信息
     * @param id
     * @param name
     * @param parentid
     * @return
     */
    String updateCompany(Integer id, String name, Integer parentid);

    /**
     * 删除公司信息
     * @param id
     * @param name
     * @param parentid
     * @return
     */
    String deleteCompany(Integer id, String name, Integer parentid);

    /**
     * 获取部门列表
     * @return
     */
    HashMap<String,Object> getDepartmentList(String name, String companyid, Integer page, Integer pageSize);

    /**
     * 获取部门树
     * @return
     */
    List<DepartmentEntity> getDepartmentEntityList();
    /**
     * 获取部门人员
     * @return
     */
    HashMap<String,Object> getDeptUsers(Integer id, Integer page, Integer pageSize);

    /**
     * 获取部门人员树
     * @return
     */
    List<UserEntity> getDeptUserTree();

    /**
     * 根据公司下的部门
     * @param companyId
     * @return
     */
    List<DepartmentEntity> getDeptByCompany(Integer companyId);
    /**
     * 增加部门
     * @param name
     * @param parentid
     * @param manager
     * @param leader
     * @param companyid
     * @return
     */
    String addDepartment(String name, Integer parentid, String manager, String leader, Integer companyid);

    /**
     * 修改部门
     * @param id
     * @param name
     * @param parentid
     * @param manager
     * @param leader
     * @param companyid
     * @return
     */
    String updateDepartment(Integer id, String name, Integer parentid, String manager, String leader, Integer companyid);

    /**
     * 删除部门
     * @param id
     * @return
     */
    String deleteDepartment(Integer id);

    /**
     * 设置部门负责人
     * @param id
     * @param manager
     * @return
     */
    String setDepartmentManager(Integer id, String manager);

    /**
     * 设置部门分管领导
     * @param id
     * @param leader
     * @return
     */
    String setDepartmentLeader(Integer id, String leader);

    /**
     * 设置所属公司
     * @param id
     * @param companyid
     * @return
     */
    String updateDeptCompany(Integer id, Integer companyid);

    /**
     * 获取组织结构数据
     * @return
     */
    HashMap<String,Object> getOrgData();

    /**
     * 获取岗位列表
     * @return
     */
    List<PositionEntity> positionList();

    /**
     * 获取岗位列表--分页
     * @param page
     * @param pageSize
     * @return
     */
    HashMap<String,Object> positionListPage(String name,Integer page, Integer pageSize);

    /**
     * 根据用户的岗位
     * @param username
     * @return
     */
    List<PositionEntity> positionListByUser(String username);

    /**
     * 获取岗位成员
     * @param page
     * @param pageSize
     * @return
     */
    HashMap<String,Object>  getPositionUser(String id, Integer page, Integer pageSize);

    /**
     * 增加岗位
     * @param name
     * @return
     */
    String addPosition(String name);

    /**
     * 更新岗位信息
     * @param id
     * @param name
     * @return
     */
    String updatePosition(String id, String name);

    /**
     * 删除岗位
     * @param id
     * @return
     */
    String deletePosition(String id);

    /**
     * 删除用户的岗位
     * @param username
     * @return
     */
    String deletePositionUser(String username);

    /**
     * 获取角色列表
     * @return
     */
    List<RolesEntity> rolesList();

    /**
     * 角色部门
     * @param id
     * @return
     */
    List<SysRoleDept> roleDeptTreeselect(String id);

    /**
     * 获取角色列表--分页
     * @param page
     * @param pageSize
     * @return
     */
    HashMap<String,Object> rolesListPage(String name,Integer page, Integer pageSize);

    /**
     * 获取用户的角色
     * @param username
     * @return
     */
    List<RolesEntity> rolesListByUser(String username);

    /**
     * 获取岗位成员
     * @param page
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getRoleUsers(String id, Integer page, Integer pageSize);
    /**
     * 增加角色
     * @param sysRoleDto
     * @return
     */
    String addRoles(SysRoleDto sysRoleDto);

    /**
     * 修改角色信息
     * @param sysRoleDto
     * @return
     */
    String updateRoles(SysRoleDto sysRoleDto);

    /**
     * 删除角色
     * @param sysRoleDto
     * @return
     */
    String deleteRoles(SysRoleDto sysRoleDto);

    /**
     * 删除用户的角色
     * @param username
     * @return
     */
    String deleteRoleUser(String username);

    /**
     * 获取人员列表
     * @return
     */
    List<UserEntity> getUserEntities();

    /**
     * 获取人员列表--分页
     * @param page
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getUserList(String name,String username,
                                              String departid,Integer page,Integer pageSize);

    /**
     * 获取人员详细信息
     * @param username
     * @return
     */
    HashMap<String,Object> getUserEntity(String username);

    /**
     * 根据部门编号查找人员
     * @param departid
     * @return
     */
    List<UserEntity> getUserByDepartId(Integer departid);

    /**
     * 增加人员
     * @param param
     * @return
     */
    String addUser(UserParams param) throws BusinessException;

    /**
     * 修改人员信息
     * @param param
     * @return
     */
    String updateUser(UserParams param);

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     * @return
     */
    String updatePwd(String username, String oldPwd, String newPwd);

    /**
     * 重置密码
     * @param username
     * @return
     */
    String resetPwd(String username);

    /**
     * 删除用户
     * @param userEntity
     * @return
     */
    String deleteUser(UserEntity userEntity);

    /**
     * 获取用户分组
     * @param name
     * @return
     */
    List<GroupEntity> getGroupList(String name);

    /**
     * 获取用户分组分页
     * @param name
     * @param page
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getGroupPage(String name, Integer page, Integer pageSize);

    /**
     * 获取分组内成员
     * @param groupId
     * @return
     */
    HashMap<String,Object> getUserByGorupId(String groupId, Integer page,
                                      Integer pageSize);

    /**
     * 增加分组
     * @param groupEntity
     * @return
     */
    String addGroup(GroupEntity groupEntity);

    /**
     * 更新分组信息
     * @param groupEntity
     * @return
     */
    String updataGroup(GroupEntity groupEntity);

    /**
     * 删除分组
     * @param groupEntity
     * @return
     */
    String deleteGroup(GroupEntity groupEntity);

    /**
     * 增加分组人员
     * @param param
     * @return
     */
    String addUserGroup(GroupUsersParams param);


    /**
     * 删除分组人员
     * @param groupid
     * @return
     */
    String deleteUserGroup(String username, String groupid);
}