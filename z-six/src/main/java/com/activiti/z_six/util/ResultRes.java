package com.activiti.z_six.util;

public class ResultRes {
    private Integer error;
    private String msg;
    private Object result;

    public Integer geterror() {
        return error;
    }

    public void seterror(Integer error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object obj) {
        this.result = obj;
    }

    private ResultRes(Integer status, String msg, Object obj) {
        this.error = status;
        this.msg = msg;
        this.result = obj;
    }

    public static ResultRes success(Object obj) {
        return new ResultRes(SystemConfig.ResponseCode.SUCCESS.getCode(),
                SystemConfig.ResponseCode.SUCCESS.getDesc(),
                obj);
    }
    public static ResultRes error(Object obj) {
        return new ResultRes(SystemConfig.ResponseCode.ERROR.getCode(),
                SystemConfig.ResponseCode.ERROR.getDesc(),
                obj);
    }
}
