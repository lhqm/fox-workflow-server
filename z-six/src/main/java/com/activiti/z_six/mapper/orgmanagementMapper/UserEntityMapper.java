package com.activiti.z_six.mapper.orgmanagementMapper;

import com.activiti.z_six.entity.orgmanagement.DepartmentEntity;
import com.activiti.z_six.entity.orgmanagement.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@Repository
public interface UserEntityMapper {
    /**
    获取人员列表
     */
    List<UserEntity> getUserEntityList(@Param("name") String name,@Param("username") String username,
                                       @Param("departid") String departid);

    /**
     * 获取人员列表分页
     * @param page
     * @param pageSize
     * @return
     */
    List<UserEntity> getUserList(@Param("name") String name,@Param("username") String username,
                                 @Param("departid") String departid,@Param("page") Integer page,
                                 @Param("pageSize") Integer pageSize);

    /**
     * 获取部门人员
     * @return
     */
    List<UserEntity> getDeptUsers();
    /**
    获取人员信息
     */
    UserEntity getUserEntityInfo(String userName);

    /**
     * 获取人员的负责人
     * @param username
     * @return
     */
    UserEntity getUserManager(@Param("username") String username);

    /**
     * 获取人员的分管领导
     * @param username
     * @return
     */
    UserEntity getUserLeader(@Param("username") String username);
    /**
    根据部门编号查找人员
     */
    List<UserEntity> getUserByDepartId(@Param("departid")Integer departid);
    /**
     根据部门编号查找人员
     */
    List<UserEntity> getUserByDepartIdPage(@Param("departid")Integer departid,
                                           @Param("page")Integer page,
                                           @Param("pagesize")Integer pagesize);
    /**
     * 根据角色编号查找人员
     * @param roleId
     * @return
     */
    List<UserEntity> getUserByRoleId(@Param("roleId")String roleId);

    /**
     * 获取角色成员
     * @param roleId
     * @param page
     * @param pagesize
     * @return
     */
    List<UserEntity> getUserByRoleIdPage(@Param("roleId")String roleId,
                                     @Param("page")Integer page,
                                     @Param("pagesize")Integer pagesize);

    /**
     * 根据岗位编号查找人员
     * @param positionId
     * @return
     */
    List<UserEntity> getUserByPositionId(@Param("positionId")String positionId);
    /**
     * 根据岗位编号查找人员
     * @return
     */
    List<UserEntity> getUserByPositionIdPage(@Param("positionId")String positionId,
                                             @Param("page")Integer page,
                                             @Param("pagesize")Integer pagesize);

    /**
     * 增加人员
     * @param userEntity
     * @return
     */
    int addUser(UserEntity userEntity);

    /**
     * 增加人员部门
     * @param userEntity
     * @return
     */
    int addDeptUser(UserEntity userEntity);

    /**
     * 增加人员岗位
     * @param
     * @return
     */
    int addUserPosition(@Param("guuid")String guuid,@Param("username")String username,@Param("psid")String psid);

    /**
     * 增加人员角色
     * @param guuid
     * @param username
     * @param roleid
     * @return
     */
    int addUserRoles(@Param("guuid")String guuid,@Param("username")String username,@Param("roleid")String roleid);

    /**
     * 修改人员信息
     * @param userEntity
     * @return
     */
    int updateUser(UserEntity userEntity);

    /**
     * 修改密码
     * @param username
     * @param pwd
     * @return
     */
    int updatePwd(@Param("username") String username,@Param("pwd") String pwd);

    /**
     * 删除用户
     * @param userEntity
     * @return
     */
    int deleteUser(UserEntity userEntity);

}
