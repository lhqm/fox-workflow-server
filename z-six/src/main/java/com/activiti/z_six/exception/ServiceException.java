package com.activiti.z_six.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 5564446583860234738L;

    private String code;
    private String errorMsg;

    public ServiceException(BaseErrorInfo baseErrorInfo){
        this.code = baseErrorInfo.getCode();
        this.errorMsg = baseErrorInfo.getMessage();
    }

    public ServiceException(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public ServiceException(String message, String code, String errorMsg) {
        super(message);
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public ServiceException(String message, Throwable cause, String code, String errorMsg) {
        super(message, cause);
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public ServiceException(Throwable cause, String code, String errorMsg) {
        super(cause);
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String errorMsg) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.errorMsg = errorMsg;
    }
}
