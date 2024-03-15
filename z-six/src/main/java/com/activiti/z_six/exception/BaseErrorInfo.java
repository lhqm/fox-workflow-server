package com.activiti.z_six.exception;

/**
 * 基础错误接口
 */
public interface BaseErrorInfo {

    //错误码
    String getCode();
    //错误描述
    String getMessage();

}