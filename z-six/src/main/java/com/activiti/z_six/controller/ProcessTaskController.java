package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.CCParams;
import com.activiti.z_six.dto.controllerParams.ProcessTaskParams;
import com.activiti.z_six.dto.controllerParams.TaskManageParams;
import com.activiti.z_six.entity.taskAssignee.*;
import com.activiti.z_six.service.IApprovalTrackService;
import com.activiti.z_six.service.IProcessTaskService;
import com.activiti.z_six.util.ResultRes;

import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ProcessTaskController {
    @Autowired
    private IProcessTaskService processTaskService;
    @Autowired
    private IApprovalTrackService approvalTrackService;

    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取任务表单内容")
    @PostMapping("/task/getHisFormJson")
    public ResultRes getHisFormJson(@RequestBody JSONObject param){
        String procKey=param.getString("procKey");
        String taskid=param.getString("taskid");
        try{
            return ResultRes.success(processTaskService.getHisFormJson(procKey,taskid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "启动流程")
    @PostMapping("/task/startProcess")
    public ResultRes startProcess(@RequestBody ProcessTaskParams processTaskParams){
        try{
            //TODO:启动流程的核心代码
            return ResultRes.success(processTaskService.startProcess(processTaskParams));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "发送流转")
    @PostMapping("/task/sendWork")
    public ResultRes sendWork(@RequestBody ProcessTaskParams processTaskParams){
        HashMap<String,Object> variables=processTaskParams.getVariables();
        variables.put("isPass", "0");
        processTaskParams.setVariables(variables);
        try{
            return ResultRes.success(processTaskService.sendWork(processTaskParams));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "驳回")
    @PostMapping("/task/returnWork")
    public ResultRes returnWork(@RequestBody ReturnWorkEntity returnWorkEntity){
        OvTaskEntity ovTaskEntity=processTaskService.returnWork(returnWorkEntity);
        if(ovTaskEntity==null){
            return ResultRes.error("退回失败");
        }
        else{
            return ResultRes.success(ovTaskEntity);
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "移交")
    @PostMapping("/task/transfer")
    public ResultRes transfer(@RequestBody TaskManageParams params){
        OvTaskEntity ovTaskEntity=processTaskService.transfer(params);
        if(ovTaskEntity==null){
            return ResultRes.error("移交失败");
        }
        else{
            return ResultRes.success(ovTaskEntity);
        }
    }

    /**
     * 批量移交
     * @param params
     * @return
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "批量移交")
    @PostMapping("/task/transferBatch")
    public ResultRes transferBatch(@RequestBody TaskManageParams params){
        try{
            return ResultRes.success(processTaskService.transferBatch(params));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "加签")
    @PostMapping("/task/countersign")
    public ResultRes countersign(@RequestBody TaskManageParams params){
        OvTaskEntity ovTaskEntity=processTaskService.countersign(params);
        if(ovTaskEntity==null){
            return ResultRes.error("加签失败");
        }
        else{
            return ResultRes.success(ovTaskEntity);
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "拒绝")
    @PostMapping("/task/refuse")
    public ResultRes refuse(@RequestBody TaskManageParams params){
        try {
            processTaskService.refuse(params);
            return ResultRes.success("申请已拒绝");
        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }

    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "手动结束流程")
    @PostMapping("/task/setEndTask")
    public ResultRes setEndTask(@RequestBody TaskManageParams params){
        try {
            processTaskService.setEndTask(params);
            return ResultRes.success("流程已经结束");

        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 批量结束
     * @param params
     * @return
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "批量结束流程")
    @PostMapping("/task/setEndTaskBatch")
    public ResultRes setEndTaskBatch(@RequestBody TaskManageParams params){
        try {
            return ResultRes.success(processTaskService.setEndTaskBatch(params));

        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除流程
     * @param params
     * @return
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.DELETE , operDesc = "删除流程实例与任务")
    @PostMapping("/task/deleteProcInst")
    public ResultRes deleteProcInst(@RequestBody TaskManageParams params){
        try {
            processTaskService.deleteProcInst(params);
            return ResultRes.success("流程已经删除");

        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取流程轨迹")
    @PostMapping("/task/getUserTaskTrack")
    public ResultRes getUserTaskTrack(@RequestBody ApprovalTrack approvalTrack){
        try{
            return ResultRes.success(approvalTrackService.getUserTaskTrack(approvalTrack));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取下一步节点信息
     * @param param
     * @return
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取下一步节点信息")
    @PostMapping("/task/getNextNodeInfo")
    public ResultRes getNextNodeInfo(@RequestBody JSONObject param){
        try{
            String procInstId=param.getString("procInstId");
            String taskid=param.getString("taskid");
            String mapString=param.getString("variables");

            return ResultRes.success(processTaskService.getNextTaskInfo(procInstId,taskid,new HashMap<>()));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取下一步接收人信息
     * @param param
     * @return
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取下一步接收人信息")
    @PostMapping("/task/getNextUserInfo")
    public ResultRes getNextUserInfo(@RequestBody JSONObject param){
        try{
            String procInstId=param.getString("procInstId");
            String taskid=param.getString("taskid");
            return ResultRes.success(processTaskService.getNextUserInfo(procInstId,taskid,new HashMap<>()));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取手动选择接收人列表
     * @param params
     * @return
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取手动选择人接收列表")
    @PostMapping("/task/getSelectNextUsers")
    public ResultRes getSelectNextUsers(@RequestBody JSONObject params){
        String bmsType=params.getString("bmsType");
        String task_def_id=params.getString("task_def_id");
        Integer page=params.getInteger("page");
        Integer pagesize=params.getInteger("pagesize");
        try {
            return ResultRes.success(processTaskService.getSelectNextUsers(bmsType,task_def_id,page,pagesize));
        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 设置下一步接收人（手动选择有效）
     * @param manuallySelectEntityList
     * @return
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "设置下一步任务处理人")
    @PostMapping("/task/setNextToUsers")
    public ResultRes setNextToUsers(@RequestBody List<ManuallySelectEntity> manuallySelectEntityList){
        try {
            return ResultRes.success(processTaskService.setNextToUsers(manuallySelectEntityList));
        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "触发等待任务")
    @PostMapping("/task/sendReceiceTask")
    public ResultRes sendReceiceTask(@RequestBody JSONObject param){
        String processInstanceId=param.getString("processInstanceId");
        String taskKey=param.getString("taskKey");
        String executionId=param.getString("executionId");
        try {
            return ResultRes.success(processTaskService.sendReceiceTask(processInstanceId,taskKey,executionId));
        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取待审核列表")
    @PostMapping("/task/getTodoList")
    public ResultRes getTodoList(@RequestBody JSONObject param){
        try{
            int pageNum=param.getInteger("pageNum");
            int pagesize=param.getInteger("pageSize");
            int startIndex = (pageNum - 1) * pagesize;
            String flowName=param.getString("flowName");
            return ResultRes.success(processTaskService.getTodoList(startIndex,pagesize,flowName));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取待审核列表")
    @GetMapping("/task/getTodoTask")
    public ResultRes getTodoTask(HttpServletResponse response,
                                 @RequestParam("flowSort") String flowSort,
                                 @RequestParam("proceId") String proceId,
                                 @RequestParam("page") Integer page,
                                 @RequestParam("pagesize") Integer pagesize){
        try {
            return ResultRes.success(processTaskService.getTodoTask(flowSort,proceId,page,pagesize));
        }
        catch (Exception ex)
        {
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取已审核列表")
    @PostMapping("/task/getHistoricTaskInstance")
    public ResultRes getHistoricTaskInstance(@RequestBody JSONObject params){
        try {
            String flowName=params.getString("flowName");
            Integer page=params.getInteger("pageNum");
            Integer pagesize=params.getInteger("pageSize");
            return ResultRes.success(processTaskService.getHistoricTaskInstance(flowName,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取审核按钮")
    @GetMapping("/task/getElementButton")
    public ResultRes getElementButton(HttpServletResponse response,
                                      @RequestParam("taskid") String taskid){
        try {
            return ResultRes.success(processTaskService.getElementButton(taskid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /***
     * 获取轨迹图/流程图
     * @param response
     * @param httpServletResponse
     * @param processInstanceId
     * @param porcessKey
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取轨迹图")
    @GetMapping(value = "/task/getTrack")
    public void processImageGet(HttpServletResponse response,
                                HttpServletResponse httpServletResponse,
                                @RequestParam("processInstanceId") String processInstanceId,
                                @RequestParam("porcessKey") String porcessKey) {
        try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
            InputStream img = approvalTrackService.getFlowImgByProcInstId(processInstanceId,porcessKey);
            byte[] bytes = IOUtils.toByteArray(img);
            httpServletResponse.setContentType("image/svg+xml");
            outputStream.write(bytes);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * 获取审核记录表
     */
    @OperLog(operModul = "流程审核" , operType = LogConst.OTHER , operDesc = "获取审核记录")
    @PostMapping(value = "/task/getApprovalTracks")
    public ResultRes getApprovalTracks(@RequestBody ApprovalTrack approvalTrack) {
        try {
            return ResultRes.success(approvalTrackService.approvalTracks(approvalTrack));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
}
