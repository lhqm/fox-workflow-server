package com.activiti.z_six.mapper.orgmanagementMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface UserGroupMapper {
    /**
     * 增加分组成员
     * @param groupUser
     * @return
     */
    int addUserGroup(@Param("groupUser") List<HashMap<String,Object>> groupUser);

    /**
     * 删除分组成员
     * @return
     */
    int deleteUserGroup(@Param("username") String username,@Param("groupid") String groupid);
}
