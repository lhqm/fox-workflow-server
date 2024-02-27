package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.OperLogParams;
import com.activiti.z_six.operLog.service.OperLogService;
import com.activiti.z_six.service.ISmsEntityService;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class SystemConfigController {
    @Autowired
    private ISmsEntityService smsEntityService;
    @Autowired
    private OperLogService operLogService;

    /**
     * 获取抄送消息列表
     * @param param
     * @return
     */
    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "获取知会消息列表")
    @PostMapping("/system/getCCSmsList")
    public ResultRes getCCSmsList(@RequestBody JSONObject param){
        HashMap<String,Object> hashMap=new HashMap<>();
        try{
            int pageNum=param.getInteger("pageno");
            int pagesize=param.getInteger("pagesize");
            String state=param.getString("status");
            String username=param.getString("username");
            int startIndex = (pageNum - 1) * pagesize;
            int maxIndex = startIndex + pagesize;
            return ResultRes.success(smsEntityService.ccSmsList(username,startIndex,maxIndex,state));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取消息列表
     * @param param
     * @return
     */
    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "获取消息列表")
    @PostMapping("/system/getSmsList")
    public ResultRes getSmsList(@RequestBody JSONObject param){
        HashMap<String,Object> hashMap=new HashMap<>();
        try{
            int pageNum=param.getInteger("pageno");
            int pagesize=param.getInteger("pagesize");
            String state=param.getString("status");
            String username=param.getString("username");
            int startIndex = (pageNum - 1) * pagesize;
            int maxIndex = startIndex + pagesize;
            return ResultRes.success(smsEntityService.smsList(username,startIndex,maxIndex,state));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "获取消息内容")
    @PostMapping("/system/getSms")
    public ResultRes getSms(@RequestBody JSONObject param){
        try{
            String id=param.getString("id");
            return ResultRes.success(smsEntityService.smsEntity(id));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 设置消息已读
     * @param param
     * @return
     */
    @OperLog(operModul = "系统参数相关" , operType = LogConst.UPDATE , operDesc = "设置消息已读")
    @PostMapping("/system/readSms")
    public ResultRes readSms(@RequestBody JSONObject param){
        String id=param.getString("id");
        String msg =smsEntityService.updateState(id);
        if(msg.equals("suess")) {
            return ResultRes.success("执行成功");
        }
        else{
            return ResultRes.error(msg);
        }
    }

    /**
     * 删除消息
     * @param param
     * @return
     */
    @OperLog(operModul = "系统参数相关" , operType = LogConst.DELETE , operDesc = "删除消息")
    @PostMapping("/system/deleteSms")
    public ResultRes deleteSms(@RequestBody JSONObject param){
        String id=param.getString("id");
        String msg =smsEntityService.deleteSms(id);
        if(msg.equals("suess")) {
            return ResultRes.success("执行成功");
        }
        else{
            return ResultRes.error(msg);
        }
    }

    /**
     * 查询系统操作日志
     * @param param
     * @return
     */
    @PostMapping("/system/operLog")
    public ResultRes operLog(@RequestBody OperLogParams param){
        return ResultRes.success(operLogService.findOperLogPage(param));
    }

    /**
     * 查询系统异常日志
     * @param param
     * @return
     */
    @PostMapping("/system/deleteOperLog")
    public ResultRes deleteOperLog(@RequestBody OperLogParams param){
        return ResultRes.success(operLogService.deleteOperLog(param));
    }
}
