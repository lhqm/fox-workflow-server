package com.activiti.z_six.util.req;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestUtils {

    public static HttpServletRequest getCurrentRequest() {
        // 使用 RequestContextHolder 获取当前请求的 ServletRequestAttributes
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        // 从 ServletRequestAttributes 中获取 HttpServletRequest 对象
        if (attributes != null) {
            return attributes.getRequest();
        }
        
        // 如果当前没有请求，则返回 null
        return null;
    }

    public static Map<String, String> getAllRequestHeaders() {
        HttpServletRequest request = getCurrentRequest();
        Map<String, String> headerMap = new HashMap<>();
        // 获取所有请求头的键值对
        if (request != null) {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    headerMap.put(headerName, headerValue);
                }
            }
        }
        return headerMap;
    }
}