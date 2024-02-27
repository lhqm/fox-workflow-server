package com.activiti.z_six.common;

public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 445445701327085003L;
    private int status;

    public BusinessException() {
        this(10000, "服务器内部错误");
    }

    public BusinessException(String message) {
        this(10001, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.status = code;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
