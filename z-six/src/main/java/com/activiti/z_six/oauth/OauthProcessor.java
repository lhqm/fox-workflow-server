package com.activiti.z_six.oauth;

import com.activiti.z_six.entity.UserInfo;
import com.activiti.z_six.exception.ServiceException;
import com.activiti.z_six.service.IUserInfoService;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * oauth2客户端处理器
 * @author 离狐千慕
 * @version 1.0
 * @date 2023/12/13 10:05
 */
@Component
@ConfigurationProperties(prefix = "oauth2.client")
public class OauthProcessor {

    @Value("${oauth2.client.clientId}")
    private String clientId;
    @Value("${oauth2.client.clientSecret}")
    private String clientSecret;
    @Value("${oauth2.client.grantType}")
    private String grantType;
    @Value("${oauth2.client.responseType}")
    private String responseType;
    @Value("${oauth2.client.redirectUri}")
    private String redirectUri;
    @Value("${oauth2.client.redirectUri-mobile}")
    private String redirectUriMobile;
    @Value("${oauth2.client.accessTokenUri}")
    private String accessTokenUri;
    @Value("${oauth2.client.userAuthorizationUri}")
    private String userAuthorizationUri;
    @Value("${oauth2.client.userInfoUri}")
    private String userInfoUri;
    @Value("${oauth2.client.scope}")
    private String scope;
    @Value("${oauth2.client.userLoginUri}")
    private String userLoginUri;

    @Autowired
    private OauthUserService oauthUserService;
    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 获取授权服务器的重定向地址
     * @return
     */

    public String getUrlToOauthCenter(Boolean isMobile){
        if (isMobile==null || !isMobile){
            return userLoginUri+"?response_type="+responseType+"&client_id="+clientId+"&redirect_uri="+redirectUri+"&scope="+scope+"&authorizationUri="+userAuthorizationUri;
        }else {
            return userLoginUri+"?response_type="+responseType+"&client_id="+clientId+"&redirect_uri="+redirectUriMobile+"&scope="+scope+"&authorizationUri="+userAuthorizationUri;
        }
    }

    /**
     * 授权码模式校验该用户的code是否有效
     * 并且取到access_token
     * 通过access_token获取用户信息，再判断是否加入子系统
     *
     * @param code
     * @param terminal
     * @return
     */
    @SneakyThrows
    public ResultRes codeAuthorization(String code, Integer terminal) {
//        判空
        if(code==null){return ResultRes.error("授权码为空，无法校验");}
//        到授权服务器远程校验,获取token
        String token=getAccessToken(code);
//        再通过token，到授权服务器获取用户信息
        AuthUser user = getUserInfo(token);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(user.getAccount());
//        通过用户账户，校验用户是否存在或者已经绑定，并且自动返回user
        UserInfo localUser = oauthUserService.getUserInLocalDataBase(userInfo);
//      通过上一步校验获得的User，通过快速登录的方式执行登录，并写入安全上下文
//        创建登录用户实例
        UserInfo loginUser;
        loginUser=localUser;
//        将用户手动重写到认证类内通过认证，写入安全上下文
        OauthUserQuickLoginAuthenticationToken authenticationToken = new OauthUserQuickLoginAuthenticationToken(loginUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //封装返回用户信息
        HashMap<String, Object> tokens = userInfoService.getToken(loginUser.getUsername(), loginUser.getPassword());
        return ResultRes.success("登录成功",tokens);
    }

    /**
     * 通过授权码获取access_token
     * @param code
     * @return
     */
    private String getAccessToken(String code) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType,
                "grant_type="+grantType +
                        "&code="+code +
                        "&client_id="+clientId +
                        "&client_secret="+clientSecret);
        Request request = new Request.Builder()
                .url(accessTokenUri)
                .post(body)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try (Response response = client.newCall(request).execute()) {
            // 处理响应
//            响应失败
            if (!response.isSuccessful()) {
                System.out.println(response.code());
                System.out.println(response.body());
                throw new ServiceException("502","授权服务器请求失败");
            }
//            响应成功
            String responseData = response.body().string();
            JSONObject res = JSON.parseObject(responseData);
//            状态不是200，报错
            if(res.getInteger("code")!=200){
                throw new ServiceException("504",res.getString("msg"));
            }
//            状态200，返回数据
            String string = res.getJSONObject("data").getString("access_token");
            System.out.println(string);
            return string;
        } catch (IOException e) {
            // 抛异常
            throw new ServiceException("502","授权服务器连接出现故障");
        }
    }
    private AuthUser getUserInfo(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "access_token="+token);
        Request request = new Request.Builder()
                .url(userInfoUri)
                .method("POST", body)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
//            发送请求
            Response response = client.newCall(request).execute();
//            校验返回
            if(!response.isSuccessful()){
                System.out.println(userInfoUri);
                System.out.println(response.code());
                System.out.println(response.body().string());
                throw new ServiceException("502","授权服务器网络异常");
            }
//            获取并解析数据
            String responseData = response.body().string();
            JSONObject res = JSONObject.parseObject(responseData);
//            授权服务器检测到其他问题
            if(res.getInteger("code")!=200){
                throw new ServiceException("504",res.getString("msg"));
            }
            JSONObject data = res.getJSONObject("data");
//            实例化用户
            AuthUser authUser = JSON.toJavaObject(data, AuthUser.class);
//            返回
            return authUser;
        } catch (IOException e) {
            throw new ServiceException("500","远程网络服务异常");
        }
    }
}
