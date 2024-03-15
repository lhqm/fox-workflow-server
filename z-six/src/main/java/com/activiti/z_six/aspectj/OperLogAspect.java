package com.activiti.z_six.aspectj;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.excLog.dto.InAddExcLogDto;
import com.activiti.z_six.excLog.service.ExcLogService;
import com.activiti.z_six.operLog.dto.InAddOperLogDto;
import com.activiti.z_six.operLog.service.OperLogService;
import com.activiti.z_six.util.AsyncManager;
import com.activiti.z_six.util.IPUtil;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * 切面处理类，操作日志异常日志记录处理
 *
 * @author zn
 * @date 2021/03/17
 */
@Aspect
@Component
@SuppressWarnings("all")
public class OperLogAspect {

    @Autowired
    private OperLogService operLogService;
    @Autowired
    private ExcLogService excLogService;

    private String username(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(SystemConfig.IsNullOrEmpty(authentication))
        {
            return "null";
        }
        String username=authentication.getName();
        return username;
    }
    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.activiti.z_six.aspectj.annotation.OperLog)")
    public void operLogPoinCut() {
    }


    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param obj       返回结果
     */
    @AfterReturning(value = "operLogPoinCut()", returning = "obj")
    public void saveOperLog(JoinPoint joinPoint, Object obj) {

        Signature sig = joinPoint.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        // 获取操作
        OperLog opLog = method.getAnnotation(OperLog.class);
        if (opLog == null) {
            return;
        }

        //获取用户的请求
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        InAddOperLogDto operlog = new InAddOperLogDto();
        try {
            operlog.setOperModule(opLog.operModul()); // 操作模块
            operlog.setOperType(opLog.operType()); // 操作类型
            operlog.setOperDesc(opLog.operDesc()); // 操作描述
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            operlog.setOperMethod(methodName); // 请求方法
            operlog.setOperResParam(JSON.toJSONString(obj)); // 返回结果
            //请求的参数
            if(method.getName().equals("loginAs")){
                operlog.setOperUserId(argsArrayToUserName(joinPoint.getArgs())); // 请求用户ID
            }
            else {
                operlog.setOperReqParam(argsArrayToString(joinPoint.getArgs())); // 请求参数
                operlog.setOperUserId(this.username()); // 请求用户ID
            }
            operlog.setOperIp(IPUtil.getIp(request)); // 请求IP
            operlog.setOperUri(request.getRequestURI()); // 请求URI
            operlog.setOperReqMethod(request.getMethod());
            // 保存数据库
            operLogService.addOperLog(operlog);

//            AsyncManager.me().execute(new TimerTask() {
//                @Override
//                public void run() {
//                    SpringUtils.getBean(OperLogService.class).addOperLog(operlog);
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 程序异常返回通知，拦截用户操作日志 .
     *
     * @param point
     * @param e
     */
    @AfterThrowing(pointcut = "operLogPoinCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {

        Signature sig = joinPoint.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        // 获取操作
        OperLog opLog = method.getAnnotation(OperLog.class);
        if (opLog == null) {
            return;
        }

        //获取用户的请求
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        InAddExcLogDto excLogDto = new InAddExcLogDto();
        try {
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            excLogDto.setOperMethod(methodName);
            // 请求的参数
            excLogDto.setExcReqParam(argsArrayToString(joinPoint.getArgs())); // 请求参数
            excLogDto.setExcName(e.getClass().getName()); // 异常名称
            excLogDto.setExcMessage(stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace())); // 异常信息
            excLogDto.setOperUserId(this.username()); // 请求用户ID
            excLogDto.setOperReqMethod(request.getMethod()); // 请求方式
            excLogDto.setOperIp(IPUtil.getIp(request)); // 请求IP
            excLogDto.setOperUri(request.getRequestURI()); // 请求URI

            // 保存数据库
            excLogService.addExcLog(excLogDto);
//            AsyncManager.me().execute(new TimerTask() {
//                @Override
//                public void run() {
//                    SpringUtils.getBean(ExcLogService.class).addExcLog(excLogDto);
//                }
//            });

        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /**
     * 转换request 请求参数
     *
     * @param paramMap request获取的参数数组
     */
    public Map<String, String> converMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<String, String>();
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }

    /**
     * 转换异常信息为字符串
     *
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        return message;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                if (!isFilterObject(paramsArray[i])) {
                    Object jsonObj = JSON.toJSON(paramsArray[i]);
                    params += jsonObj==null?"":jsonObj.toString() + " ";
                }
            }
        }
        return params.trim();
    }

    /**
     * 获取参数中的用户名，登录验证时使用
     * @param paramsArray
     * @return
     */
    private String argsArrayToUserName(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                if (!isFilterObject(paramsArray[i])) {
                    JSONObject josn=JSONObject.parseObject(paramsArray[i].toString());
                    params=josn.getString("username");
                }
            }
        }
        return params.trim();
    }
    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    public boolean isFilterObject(final Object o) {
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }
}