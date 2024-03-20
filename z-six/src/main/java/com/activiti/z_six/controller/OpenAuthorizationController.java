package com.activiti.z_six.controller;

import com.activiti.z_six.oauth.OauthProcessor;
import com.activiti.z_six.util.ResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ram")
public class OpenAuthorizationController {
    @Autowired
    private OauthProcessor oauthProcessor;
    @GetMapping("/redirectUrl")
    public ResultRes getUrlToOauthCenter(Boolean isMobile) {
        return ResultRes.success(oauthProcessor.getUrlToOauthCenter(isMobile));
    }
    @GetMapping("/codeAuth")
    public ResultRes getToken(String code, Integer terminal) {
        return oauthProcessor.codeAuthorization(code, terminal);
    }
}
