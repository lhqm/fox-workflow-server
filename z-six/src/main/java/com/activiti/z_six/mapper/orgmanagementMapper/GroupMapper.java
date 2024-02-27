package com.activiti.z_six.mapper.orgmanagementMapper;

import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.entity.orgmanagement.GroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface GroupMapper {
    /**
     * 获取用户组
     * @param name
     * @return
     */
    List<GroupEntity> getGroupList(@Param("name") String name);

    /**
     * 获取用户组分页
     * @param name
     * @param page
     * @param pagesize
     * @return
     */
    List<GroupEntity> getGroupPage(@Param("name") String name,
                                       @Param("page")Integer page,
                                       @Param("pagesize")Integer pagesize);

    /**
     * 获取用户组下的人员
     * @param groupId
     * @return
     */
    List<UserEntity> getGroupUserPage(@Param("groupId") String groupId,@Param("page")Integer page,
                                      @Param("pageSize")Integer pageSize);
    /**
     * 获取分组内成员
     * @param groupId
     * @return
     */
    List<UserEntity> getUserByGorupId(@Param("groupId") String groupId);

    /**
     * 增加用户组
     * @param groupEntity
     * @return
     */
    int addGroup(GroupEntity groupEntity);

    /**
     * 更新用户组
     * @param groupEntity
     * @return
     */
    int updataGroup(GroupEntity groupEntity);

    /**
     * 删除用户组
     * @param groupId
     * @return
     */
    int deleteGroup(@Param("groupId") String groupId);
}
