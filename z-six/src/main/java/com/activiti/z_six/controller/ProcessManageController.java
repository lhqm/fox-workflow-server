package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.StatAnalysisParams;
import com.activiti.z_six.service.IProcessManageService;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessManageController {
    @Autowired
    private IProcessManageService processManageService;
    /**
     * 获取所有启动的流程实例
     * @param params
     * @return
     */
    @OperLog(operModul = "流程实例相关" , operType = LogConst.OTHER , operDesc = "获取所有启动的流程实例")
    @PostMapping(value = "/processManage/getProcessManageListPage")
    public ResultRes getProcessManageListPage(@RequestBody JSONObject params) {
        String title=params.getString("title");
        String flowName=params.getString("flowName");
        Integer page=params.getInteger("page");
        Integer pagesize=params.getInteger("pageSize");
        String state=params.getString("state");
        try{
            return ResultRes.success(processManageService.getProcessManageListPage(title,flowName,state,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "流程实例相关" , operType = LogConst.OTHER , operDesc = "获取流程实例参数")
    @PostMapping(value = "/processManage/getManageParams")
    public ResultRes getManageParams(@RequestBody JSONObject params) {
        String proce_inst_id=(params.get("business_key") instanceof String)?
                params.getString("proce_inst_id"):params.getJSONObject("business_key").getString("proce_inst_id");
        String business_key=params.getString("business_key");
        try{
            return ResultRes.success(processManageService.getManageParams(proce_inst_id,business_key));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    /**
     * 获取所有任务分页
     * @param params
     * @return
     */
    @OperLog(operModul = "流程实例相关" , operType = LogConst.OTHER , operDesc = "获取所有任务列表")
    @PostMapping(value = "/processManage/getTaskManageListPage")
    public ResultRes getTaskManageListPage(@RequestBody JSONObject params) {
        String title=params.getString("title");
        String userid=params.getString("userid");
        Integer page=params.getInteger("page");
        Integer pagesize=params.getInteger("pageSize");
        try{
            return ResultRes.success(processManageService.getTaskManageListPage(title,userid,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取流程统计数据
     * @param statAnalysisParams
     * @return
     */
    @OperLog(operModul = "流程实例相关" , operType = LogConst.OTHER , operDesc = "获取流程统计数据")
    @PostMapping(value = "/processManage/getStatAnalysisData")
    public ResultRes getStatAnalysisData(@RequestBody StatAnalysisParams statAnalysisParams) {
        try{
            return ResultRes.success(processManageService.getStatAnalysisData(statAnalysisParams));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取我发起的流程列表
     * @param params
     * @return
     */
    @OperLog(operModul = "流程实例相关" , operType = LogConst.OTHER , operDesc = "获取我发起的流程列表")
    @PostMapping(value = "/processManage/getMyStartListPage")
    public ResultRes getMyStartListPage(@RequestBody JSONObject params) {
        try{
            String title=params.getString("title");
            String flowName=params.getString("flowName");
            Integer page=params.getInteger("page");
            Integer pagesize=params.getInteger("pageSize");
            String state=params.getString("state");
            return ResultRes.success(processManageService.getMyStartListPage(title,flowName,state,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取我审核的流程列表
     * @param params
     * @return
     */
    @OperLog(operModul = "流程实例相关" , operType = LogConst.OTHER , operDesc = "获取我审核的流程实例列表")
    @PostMapping(value = "/processManage/getMyProcessPage")
    public ResultRes getMyProcessPage(@RequestBody JSONObject params) {
        try{
            String title=params.getString("title");
            String flowName=params.getString("flowName");
            Integer page=params.getInteger("page");
            Integer pagesize=params.getInteger("pageSize");
            String state=params.getString("state");
            return ResultRes.success(processManageService.getMyProcessPage(title,flowName,state,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
}
