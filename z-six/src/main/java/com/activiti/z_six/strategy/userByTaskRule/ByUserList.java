package com.activiti.z_six.strategy.userByTaskRule;

import com.activiti.z_six.entity.taskAssignee.AssigneeUserEntity;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 按照绑定的人员查找节点处理人，多人处理人模式
 */
@Component
public class ByUserList implements UserByTaskRuleStrategy{
    @Autowired
    private IAssigneeUserService assigneeUserService;
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public HashMap<String,Object> getUserByTaskRule(String taskid, String ruleName
            , HashMap<String,Object> variables, String procInstId){
        List<String> userList=new ArrayList<>();
        //查找绑定的接收人
        HashMap<String,Object> hashMap=assigneeUserService
                .getAssigneeUserWithTaskKey(taskid,ruleName);
        List<AssigneeUserEntity> assigneeUserEntityList = (List<AssigneeUserEntity>)hashMap.get("assigneeUserEntityList");
        String nextWorkEmp="";
        for (AssigneeUserEntity ae : assigneeUserEntityList) {
            if(nextWorkEmp.contains(ae.getUsername()+","))
                continue;
            userList.add(ae.getUsername());
            nextWorkEmp = nextWorkEmp + ae.getUsername() + ",";
        }

        //放入缓存，用于发送消息推送
        variables.put(taskid + "_byUserList", userList);
        if(redisUtils.exists(procInstId+"_sms")){
            redisUtils.remove(procInstId+"_sms");
        }
        redisUtils.set(procInstId+"_sms",nextWorkEmp);
        return variables;
    }
    @Override
    public String getType(){
        return "byUserList";
    }
}
