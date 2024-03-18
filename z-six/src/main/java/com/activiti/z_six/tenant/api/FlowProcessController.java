package com.activiti.z_six.tenant.api;

import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.tenant.model.api.FormCommit;
import com.activiti.z_six.tenant.model.api.ProcessStart;
import com.activiti.z_six.tenant.service.ProcessService;
import com.activiti.z_six.util.ResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/api")
public class FlowProcessController {

    @Autowired
    private ProcessService processService;

    /**
     * 获取任务表单内容
     * @param processKey 流程定义ID
     * @param taskId 任务ID
     * @return 表单数据
     */
    @GetMapping("/getFormJson")
    public ResultRes getFormJson(String processKey,String taskId) {
        try{
            return ResultRes.success(processService.getFormJson(processKey,taskId));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    /**
     * 提交任务表单
     * @param formCommit 流程信息
     * @return 提交回执
     */
    @PostMapping("/submitForm")
    public ResultRes submitForm(@RequestBody FormCommit formCommit) {
        try{
            return ResultRes.success(processService.submitForm(formCommit.getProcessKey(), formCommit.getDataJson(), formCommit.getTaskId()));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    /**
     * 启动流程
     * @param processTaskParams 流程信息
     * @return 根据信息去启动流程
     */
    @PostMapping("/startProcess")
    public ResultRes startProcess(@RequestBody ProcessTaskParams processTaskParams) {
        try{
            return ResultRes.success(processService.startProcess(processTaskParams));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
}
