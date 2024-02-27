package com.activiti.z_six.service;

import com.activiti.z_six.entity.UserInfo;

import java.util.HashMap;

public interface IUserInfoService {
    /**
     * 获取token
     * @param username
     * @param password
     * @return
     */
    public HashMap<String,Object> getToken(String username, String password) throws Exception;
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    public HashMap<String,Object> loginAs(String username, String password);

    /**
     * 刷新toekn
     * @return
     */
    public HashMap<String,Object> refToken(String refresh_token);
    /**
    获取用户信息
     */
    public UserInfo getUserInfo(String username);
}
