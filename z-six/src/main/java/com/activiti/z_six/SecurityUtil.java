package com.activiti.z_six;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SecurityUtil {

    private Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    @Autowired
    private UserDetailsService userDetailsService;

    public void logInAs(String username) {

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user == null) {
            throw new IllegalStateException("User " + username + " doesn't exist, please provide a valid user");
        }
        SecurityContextHolder.setContext(new SecurityContextImpl(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return user.getPassword();
            }

            @Override
            public Object getDetails() {
                return user;
            }

            @Override
            public Object getPrincipal() {
                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return user.getUsername();
            }
        }));
        org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId(username);
    }
    public void logInOnlyAct(String username) {
        org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId(username);
    }

    /**
     * 安全登录（常用于强制登录某一系统）
     * @param username 用户名
     */
    public void loginSafely(String username) {
        try {
//            根据用户名正常登录
            logInAs(username);
        }catch (Exception e){
//            没找到，直接登录默认账号进行业务
            logInAs("admin");
        }
    }
}
