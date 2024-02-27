package com.activiti.z_six.util;

import com.activiti.z_six.common.BusinessException;
import com.activiti.z_six.entity.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    /**
     * 获取用户账户
     **/
    public static String getUsername()
    {
        try
        {
            return getAuthentication().getName();
        }
        catch (Exception e)
        {
            throw new BusinessException(HttpStatus.UNAUTHORIZED.value(),"获取用户账户异常");
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
