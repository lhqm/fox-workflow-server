package com.activiti.z_six.oauth;

import com.activiti.z_six.dto.controllerParams.UserParams;
import com.activiti.z_six.entity.UserInfo;
import com.activiti.z_six.mapper.UserInfoMapper;
import com.activiti.z_six.service.IOrgManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2023/12/13 16:22
 */
@Service
public class OauthUserService {
    @Autowired
    private UserInfoMapper userMapper;
    @Autowired
    private IOrgManagementService orgManagementService;
    public UserInfo getUserByAccount(String account) {
        UserInfo user = userMapper.getUserInfo(account);
//        存在绑定账号，返回绑定的账号
//        if (user!=null && user.getBindId()!=null){
//            return userMapper.selectById(user.getBindId());
//        }
        return user;
    }

    public UserInfo saveUser(UserInfo user) {
        UserParams localUser = new UserParams();
        localUser.setUsername(user.getUsername());
        localUser.setName(user.getName());
        localUser.setPassword(user.getPassword());
//        部门置为默认
        localUser.setDepartid(10);
        orgManagementService.addUser(localUser);
        return user;
    }

    /**
     * 判断用户是否存在，存在就返回查询结果，否则自动注册账户并返回
     * @param user
     * @return
     */

    public UserInfo getUserInLocalDataBase(UserInfo user){
        UserInfo localUser = getUserByAccount(user.getUsername());
        if (localUser !=null){
            return localUser;
        }
        else {
            return saveUser(user);
        }
    }
}
