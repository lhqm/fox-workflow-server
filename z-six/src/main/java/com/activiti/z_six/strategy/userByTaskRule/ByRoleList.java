package com.activiti.z_six.strategy.userByTaskRule;

import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.entity.taskAssignee.AssigneeUserEntity;
import com.activiti.z_six.mapper.orgmanagementMapper.UserEntityMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 按照绑定的角色查找接收人，多人处理模式
 */
@Component
public class ByRoleList implements UserByTaskRuleStrategy{
    @Autowired
    private IAssigneeUserService assigneeUserService;
    @Autowired
    private UserEntityMapper userEntityMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public HashMap<String,Object> getUserByTaskRule(String taskid, String ruleName
            , HashMap<String,Object> variables, String procInstId) {
        //查找绑定的接收人
        HashMap<String, Object> hashMap = assigneeUserService
                .getAssigneeUserWithTaskKey(taskid, ruleName);
        List<AssigneeUserEntity> assigneeUserEntityList = (List<AssigneeUserEntity>) hashMap.get("assigneeUserEntityList");
        String nextWorkEmp = "";
        List<String> roleList=new ArrayList<>();

        for (AssigneeUserEntity ae : assigneeUserEntityList) {
            List<UserEntity> userEntityList=userEntityMapper.getUserByRoleId(ae.getUsername());
            for(UserEntity userEntity:userEntityList){
                if(nextWorkEmp.contains(userEntity.getUsername()+","))
                    continue;
                roleList.add(userEntity.getUsername());
                nextWorkEmp = nextWorkEmp + userEntity.getUsername() + ",";
            }
        }
        //放入缓存，用于发送消息推送
        variables.put(taskid + "_byRoleList", roleList);
        if(redisUtils.exists(procInstId+"_sms")){
            redisUtils.remove(procInstId+"_sms");
        }
        redisUtils.set(procInstId+"_sms",nextWorkEmp);
        return variables;
    }
    @Override
    public String getType(){
        return "byRoleList";
    }
}
