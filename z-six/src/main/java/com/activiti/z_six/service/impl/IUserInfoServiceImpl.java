package com.activiti.z_six.service.impl;

import com.activiti.z_six.SecurityUtil;
import com.activiti.z_six.entity.UserInfo;
import com.activiti.z_six.mapper.UserInfoMapper;
import com.activiti.z_six.security.AuthenticationContextHolder;
import com.activiti.z_six.service.IUserInfoService;
import com.activiti.z_six.util.HttpsUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Hashtable;

@Service
public class IUserInfoServiceImpl implements IUserInfoService {
    @Autowired
    private SecurityUtil securityUtil;
    @Resource
    private AuthenticationManager authenticationManager;
    @Autowired
    UserInfoMapper userInfoMapper;
    private String grant_type;
    @Value("${z6System.client_id}")
    private String client_id;
    @Value("${z6System.client_secret}")
    private String client_secret;
    @Value("${server.port}")
    private String port;

    /**
     * 获取token
     * @param username
     * @param password
     * @return
     */
    @Override
    public HashMap<String,Object> getToken(String username, String password) throws Exception{
        String url = "http://localhost:"+port+"/oauth/token?client_id="+client_id+""
                + "&client_secret="+client_secret+""
                + "&username="+username+"&password="+password+""
                + "&grant_type=password";
        //增加header参数
        java.util.Map<String, String> headerMap = new Hashtable<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("charset", "utf-8");

        //请求返回信息json
        JSONObject json = null;
        System.out.println(username+"\n"+password);
        String jsonData = HttpsUtils.post(url, headerMap, null);

        json = JSONObject.parseObject(jsonData);
        String access_token = json.getString("access_token");

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("token",access_token);
        hashMap.put("refresh_token",json.getString("refresh_token"));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationContextHolder.setContext(authenticationToken);

        return hashMap;
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public HashMap<String,Object> loginAs(String username, String password){
        HashMap<String,Object> hashMap=new HashMap<>();
        UserInfo userInfo=userInfoMapper.getUserInfo(username);
        hashMap.put("username",userInfo.getUsername());
        hashMap.put("name",userInfo.getName());

        return hashMap;
    }
    @Override
    public HashMap<String,Object> refToken(String refresh_token){
        try {
            String url = "http://localhost:"+port+"/oauth/token?client_id="+client_id+""
                    + "&client_secret="+client_secret+"&refresh_token="+refresh_token+""
                    + "&grant_type=refresh_token";
            //增加header参数
            java.util.Map<String, String> headerMap = new Hashtable<String, String>();
            headerMap.put("Content-Type", "application/json");
            headerMap.put("charset", "utf-8");

            //请求返回信息json
            JSONObject json = null;
            String jsonData = HttpsUtils.post(url, headerMap, null);

            json = JSONObject.parseObject(jsonData);
            String access_token = json.getString("access_token");

            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("token",access_token);
            hashMap.put("refresh_token",json.getString("refresh_token"));

            return hashMap;
        }
        catch (Exception ex){
            return null;
        }
    }
    /**
    根据用户名获取用户信息
     */
    @Override
    public UserInfo getUserInfo(String username){
        UserInfo userInfo=userInfoMapper.getUserInfo(username);
        if(userInfo==null) return null;
        return userInfoMapper.getUserInfo(username);
    }
}
