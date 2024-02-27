package com.activiti.z_six.aspectj.annotation;

import com.activiti.z_six.aspectj.operate.LogConst;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented
public @interface OperLog {

    /**
     * 操作模块
     * @return
     */
    String operModul() default "";

    /**
     * 操作类型
     * @return
     */
    String operType() default LogConst.OTHER;

    /**
     * 操作说明
     * @return
     */
    String operDesc() default "";
}