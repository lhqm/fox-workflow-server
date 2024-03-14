package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.ProcessDefinitionParams;
import com.activiti.z_six.dto.controllerParams.TaskDefinitionParams;
import com.activiti.z_six.service.IProcessDefinitionService;
import com.activiti.z_six.util.ResultRes;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
public class ProcessDefinitionController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IProcessDefinitionService processDefinitionService;

    /**
     * 获取流程分类
     * @param response
     * @param page
     * @param pagesize
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取流程分类列表")
    @GetMapping("/deployment/getFlowSort")
    public ResultRes getFlowSort(HttpServletResponse response,
                                 @RequestParam("page") Integer page,
                                 @RequestParam("pagesize") Integer pagesize){
        int startIndex=(page-1)*pagesize;
        int maxIndex=startIndex+pagesize;
        try {
            return ResultRes.success(processDefinitionService.getFlowSort(startIndex,maxIndex));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取全部流程分类
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取全部流程分类")
    @GetMapping("/deployment/getFlowSortAll")
    public ResultRes getFlowSortAll(){
        try {
            return ResultRes.success(processDefinitionService.getFlowSortAll());
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取流程分类树型结构数据
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取流程分类树形结构数据")
    @GetMapping("/deployment/getFlowSortTree")
    public ResultRes getFlowSortTree(){
        try {
            return ResultRes.success(processDefinitionService.getFlowSortTree());
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    /**
    获取流程列表
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取流程列表")
    @PostMapping("/deployment/getDeployment")
    public ResultRes getDeployment(@RequestBody JSONObject param){
        Integer page=param.getInteger("page");
        Integer pagesize=param.getInteger("pagesize");
        String sortid= param.getString("sortid");
        String name=param.getString("name");
        try {
            return ResultRes.success(processDefinitionService.getDeployment(page,pagesize,sortid,name));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 流程发布
     * @param param
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.INSERT , operDesc = "流程发布")
    @PostMapping(value = "/deployment/deployWithBPMNJS")
    public ResultRes deployWithBPMNJS(@RequestBody JSONObject param,String tenantId){
        String stringBPMNXml=param.get("stringBPMNXml").toString();
        try {
            return ResultRes.success(processDefinitionService.deployWithBPMNJS(stringBPMNXml,tenantId));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    /**
     * 获取流程定义的xml模版
     * @param response
     * @param deploymentId
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取流程定义的xml模版")
    @GetMapping(value="/deployment/getDeploymentXmlById")
    public void getDeploymentXmlById(HttpServletResponse response,
                                     @RequestParam("deploymentId") String deploymentId){
        try {
            InputStream inputStream = repositoryService.getResourceAsStream(deploymentId,"createWithBPMNJS.bpmn");
            int count = inputStream.available();
            byte[] bytes = new byte[count];
            response.setContentType("text/xml");
            OutputStream outputStream = response.getOutputStream();
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }
            inputStream.close();
        } catch (Exception e) {
            e.toString();
        }
    }
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取流程属性")
    @GetMapping(value="/deployment/processProperties")
    public ResultRes processProperties(@RequestParam("process_key") String process_key){
        return ResultRes.success(processDefinitionService.processProperties(process_key));
    }
    @OperLog(operModul = "流程定义相关" , operType = LogConst.INSERT , operDesc = "更新流程属性")
    @PostMapping(value = "/deployment/saveProcessProperties")
    public ResultRes saveProcessProperties(@RequestBody ProcessDefinitionParams processDefinitionParams){
        return ResultRes.success(processDefinitionService.saveProcessProperties(processDefinitionParams));
    }
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取节点属性")
    @GetMapping(value="/deployment/userTaskProperties")
    public ResultRes userTaskProperties(@RequestParam("task_def_key") String task_def_key){
        return ResultRes.success(processDefinitionService.userTaskProperties(task_def_key));
    }
    @OperLog(operModul = "流程定义相关" , operType = LogConst.INSERT , operDesc = "更新节点属性")
    @PostMapping(value = "/deployment/saveUserTaskProperties")
    public ResultRes saveUserTaskProperties(@RequestBody TaskDefinitionParams taskDefinitionParams){
        return ResultRes.success(processDefinitionService.saveUserTaskProperties(taskDefinitionParams));
    }
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取流程发起列表")
    @GetMapping(value="/deployment/getSatrtPageData")
    public ResultRes getSatrtPageData(){
        try {
            return ResultRes.success(processDefinitionService.getSatrtPageData());
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    /**
     * 增加流程分类
     * @param param
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.INSERT , operDesc = "增加流程分类")
    @PostMapping(value = "/deployment/addFlowSort")
    public ResultRes addFlowSort(@RequestBody JSONObject param){
        String name=param.get("name").toString();
        Long parentid=Long.parseLong("0");
        if(!SystemConfig.IsNullOrEmpty(param.getString("parentId"))){
            parentid=param.getLong("parentId");
        }
        try{
            return ResultRes.success(processDefinitionService.addFlowSort(name,parentid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 修改流程分类
     * @param param
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.UPDATE , operDesc = "修改流程分类")
    @PostMapping(value = "/deployment/saveFlowSort")
    public ResultRes saveFlowSort(@RequestBody JSONObject param){
        Long id=param.getLong("id");
        String name=param.get("name").toString();
        Long parentid=param.getLong("parentId");
        try{
            return ResultRes.success(processDefinitionService.saveFlowSort(id,name,parentid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 删除流程分类
     * @param param
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.DELETE , operDesc = "删除流程分类")
    @PostMapping(value = "/deployment/deleteFlowSort")
    public ResultRes deleteFlowSort(@RequestBody JSONObject param){
        Long id=param.getLong("id");
        String name=param.get("name").toString();
        Long parentid=param.getLong("parentId");
        try{
            return ResultRes.success(processDefinitionService.deleteFlowSort(id,name,parentid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 删除流程定义
     * @param response
     * @param deploymentId
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.DELETE , operDesc = "删除流程定义")
    @GetMapping("deployment/delDefinition")
    public ResultRes delDefinition(HttpServletResponse response,
                                   @RequestParam("depID") String deploymentId){
        try{
            return ResultRes.success(processDefinitionService.deleteDefinition(deploymentId));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
}
