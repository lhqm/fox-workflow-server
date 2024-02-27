package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.entity.formComponents.FormEntity;
import com.activiti.z_six.entity.formComponents.FormSortEntity;
import com.activiti.z_six.service.IFormMapService;
import com.activiti.z_six.util.ResultRes;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSONObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
public class FormMapController {
    @Autowired
    private IFormMapService formMapService;

    /**
     * 表单列表
     * @return
     */
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "获取表单列表")
    @PostMapping("/form/formList")
    public ResultRes formList(@RequestBody JSONObject param){
        String name=param.getString("name");
        String formSort=param.getString("form_type");
        if(SystemConfig.IsNullOrEmpty(formSort)){
            formSort="all";
        }
        Integer page=param.getInteger("page");
        Integer pagesize=param.getInteger("pagesize");
        return ResultRes.success(formMapService.formList(name,formSort,page,pagesize));
    }

    /**
     * 获取表单树
     * @return
     */
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "获取表单树")
    @GetMapping("/form/getFormTree")
    public ResultRes getFormTree(){
        return ResultRes.success(formMapService.getFormTree());
    }
    /**
     * 获取表单树，增加时使用
     * @return
     */
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "获取表单树")
    @GetMapping("/form/getFormTreeByAdd")
    public ResultRes getFormTreeByAdd(){
        return ResultRes.success(formMapService.getFormTreeByAdd());
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "获取/查看单个表单")
    @GetMapping("/form/getFormEntity")
    public ResultRes getFormEntity(HttpServletResponse response,
                                   @RequestParam("id") String id){
        FormEntity formEntity=formMapService.getFormEntity(id);
        if(formEntity!=null) {
            return ResultRes.success(formEntity);
        }
        else{
            return ResultRes.error("不存在id为["+id+"]的对象。");
        }
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.INSERT , operDesc = "创建表单")
    @PostMapping("/form/editForm")
    public ResultRes editForm(@RequestBody JSONObject param){
        return ResultRes.success(formMapService.editForm(param));
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.DELETE , operDesc = "删除表单")
    @PostMapping("/form/deleteForm")
    public ResultRes deleteForm(@RequestBody JSONObject param){
        String msg=formMapService.deleteForm(param.getString("id"));
        if(msg.equals("执行成功")) {
            return ResultRes.success(msg);
        }
        else{
            return ResultRes.error(msg);
        }
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "获取表单分类列表")
    @GetMapping("/form/formSortList")
    public ResultRes formSortList(){
        return ResultRes.success(formMapService.formSortList());
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "查看某个表单分类信息")
    @GetMapping("/form/getFormSort")
    public ResultRes getFormSort(HttpServletResponse response,
                                 @RequestParam("id") String id){
        FormSortEntity formSortEntity=formMapService.getFormSort(id);
        if(formSortEntity!=null) {
            return ResultRes.success(formSortEntity);
        }
        else{
            return ResultRes.error("不存在id为["+id+"]的对象。");
        }
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.INSERT , operDesc = "增加表单分类")
    @PostMapping("/form/addFormSort")
    public ResultRes addFormSort(@RequestBody JSONObject param){
        String id=UUID.randomUUID().toString();
        String name=param.getString("name");
        String parentNo=param.getString("parentNo");
        String msg=formMapService.addFormSort(id,name,parentNo);
        if(msg.equals("执行成功")) {
            return ResultRes.success(msg);
        }
        else{
            return ResultRes.error(msg);
        }
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.DELETE , operDesc = "删除表单分类")
    @PostMapping("/form/deleteFormSort")
    public ResultRes deleteFormSort(@RequestBody JSONObject param){
        String id=param.getString("id");
        String msg=formMapService.deleteFormSort(id);
        if(msg.equals("执行成功")) {
            return ResultRes.success(msg);
        }
        else{
            return ResultRes.error(msg);
        }
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.UPDATE , operDesc = "更新表单分类")
    @PostMapping("/form/updateFormSort")
    public ResultRes updateFormSort(@RequestBody JSONObject param){
        String id=param.getString("id");
        String name=param.getString("name");
        String parentNo=param.getString("parentNo");
        String msg=formMapService.updateFormSort(id,name,parentNo);
        if(msg.equals("执行成功")) {
            return ResultRes.success(msg);
        }
        else{
            return ResultRes.error(msg);
        }
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "获取表单数据")
    @GetMapping("/form/getGenerWork")
    public ResultRes getGenerWork(HttpServletResponse response,
                                  @RequestParam("id") String id){
        try{
            return ResultRes.success(formMapService.getGenerWork(id));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "表单操作" , operType = LogConst.DELETE , operDesc = "删除表单数据")
    @PostMapping("/form/deleteGenerWork")
    public ResultRes deleteGenerWork(@RequestBody JSONObject param){
        String id=param.getString("frmId");
        String list=param.getString("list");
        String msg=formMapService.deleteGenerWork(id,list);
        if(msg.equals("suess")){
            return ResultRes.success("删除成功。");
        }
        else{
            return ResultRes.error(msg);
        }
    }
}
