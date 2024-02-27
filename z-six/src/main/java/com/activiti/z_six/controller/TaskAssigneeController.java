package com.activiti.z_six.controller;

import com.activiti.z_six.SecurityUtil;
import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.entity.taskAssignee.FlowElementAttrs;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import com.activiti.z_six.service.IProcessDefinitionService;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
public class TaskAssigneeController {
    @Autowired
    private IAssigneeUserService assigneeUserService;
    @Autowired
    private RedisUtils redisUtils;

    @OperLog(operModul = "流程任务相关" , operType = LogConst.OTHER , operDesc = "获取用户任务中设置的规则数据")
    @GetMapping("/as/getAssigneeUserWithTaskId")
    public ResultRes getAssigneeUserWithTaskId(HttpServletResponse response,
                                               @RequestParam("usertaskid") String usertaskid,
                                               @RequestParam("ruleName") String ruleName){
        try{
            return ResultRes.success(assigneeUserService.getAssigneeUserWithTaskKey(usertaskid,ruleName));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "流程任务相关" , operType = LogConst.OTHER , operDesc = "设置用户任务中设置的规则数据")
    @PostMapping("/as/setAssigneeUser")
    public ResultRes setAssigneeUser(@RequestBody JSONObject param){
        return ResultRes.success(assigneeUserService.setAssigneeUser(param));
    }
    @OperLog(operModul = "流程任务相关" , operType = LogConst.OTHER , operDesc = "设置用户任务中设置的参数")
    @PostMapping("/as/setFlowElementAttrs")
    public ResultRes setFlowElementAttrs(@RequestBody JSONObject param){
        String redisKey=param.getString("redisKey");
        String attrValue=param.getString("attrKey");
        if(redisUtils.exists(redisKey)){
            redisUtils.remove(redisKey);
        }
        redisUtils.set(redisKey,attrValue);
        return ResultRes.success("设置成功");
    }

    @OperLog(operModul = "流程任务相关" , operType = LogConst.OTHER , operDesc = "删除接收人")
    @PostMapping("/as/deleteAssigneeUser")
    public ResultRes deleteAssigneeUser(@RequestBody JSONObject param){
        String usertaskid=param.getString("usertaskid");
        String username=param.getString("username");
        try{
            return ResultRes.success(assigneeUserService.deleteAssigneeUser(usertaskid,username));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
}
