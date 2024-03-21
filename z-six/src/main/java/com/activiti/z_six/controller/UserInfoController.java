package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.entity.UserInfo;
import com.activiti.z_six.service.IOrgManagementService;
import com.activiti.z_six.service.impl.IUserInfoServiceImpl;
import com.activiti.z_six.util.ResultRes;
import com.activiti.z_six.util.encode.AesPasswordEncoder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserInfoController {
    @Autowired
    IUserInfoServiceImpl userInfoService;
    @Autowired
    IOrgManagementService orgManagementService;

    @OperLog(operModul = "登录认证" , operType = LogConst.OTHER , operDesc = "获取token")
    @PostMapping(value = "/login/getToken")
    public ResultRes getToken(@RequestBody JSONObject param){
        String username=param.getString("username");
        String password=param.getString("password");
        try{
            return ResultRes.success(userInfoService.getToken(username,password));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "登录认证" , operType = LogConst.OTHER , operDesc = "内部系统登录")
    @PostMapping(value = "/login/loginAs")
    public ResultRes loginAs(@RequestBody JSONObject param){
        String username=param.getString("username");
        String password=param.getString("password");
        try{
            return ResultRes.success(userInfoService.loginAs(username,password));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "登录认证" , operType = LogConst.OTHER , operDesc = "刷新Token")
    @PostMapping(value = "/login/refToken")
    public ResultRes refToken(@RequestBody JSONObject param){
        String refresh_token=param.getString("refresh_token");
        try{
            return ResultRes.success(userInfoService.refToken(refresh_token));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    /**
    根据登录名获取用户信息
     */
    @OperLog(operModul = "用户信息" , operType = LogConst.OTHER , operDesc = "获取用户信息")
    @GetMapping(value="/login/getUserInfo")
    public ResultRes getUserInfo(@RequestParam("username") String username, @RequestParam("password") String password){
        UserInfo userInfo= userInfoService.getUserInfo(username);
        AesPasswordEncoder encoder = new AesPasswordEncoder();
        JSONObject obj=new JSONObject();
        if(userInfo!=null) {
            if(encoder.matches(password,userInfo.getPassword())) {
                obj.put("username",userInfo.getUsername());
                obj.put("name",userInfo.getName());
                return ResultRes.success(obj);
            }
            else
                return ResultRes.error("");
        }
        else
            return ResultRes.error("");
    }
}
