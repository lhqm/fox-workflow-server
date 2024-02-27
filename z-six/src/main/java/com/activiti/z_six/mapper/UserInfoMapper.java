package com.activiti.z_six.mapper;

import com.activiti.z_six.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserInfoMapper {
    /**
     * 获取用户信息
     * @param username
     * @return
     */
    UserInfo getUserInfo(String username);
}
