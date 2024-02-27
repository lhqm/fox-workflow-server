package com.activiti.z_six.strategy.userByTaskRule;

import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.entity.taskAssignee.AssigneeUserEntity;
import com.activiti.z_six.mapper.orgmanagementMapper.GroupMapper;

import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import com.activiti.z_six.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * 按照绑定的分组查找接收人
 */
@Component
public class ByGroup implements UserByTaskRuleStrategy{
    @Autowired
    private IAssigneeUserService assigneeUserService;
    @Autowired
    private GroupMapper groupMapper;
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
        for (AssigneeUserEntity ae : assigneeUserEntityList) {
            List<UserEntity> userEntityList=groupMapper.getUserByGorupId(ae.getUsername());
            for(UserEntity userEntity:userEntityList){
                nextWorkEmp = nextWorkEmp + userEntity.getUsername() + ",";
            }
        }
        if(!StringUtils.isNull(nextWorkEmp)){
            nextWorkEmp = nextWorkEmp.substring(0, nextWorkEmp.length() - 1);
        }

        //放入缓存，用于发送消息推送
        variables.put(taskid + "_byGroup", nextWorkEmp);
        if(redisUtils.exists(procInstId+"_sms")){
            redisUtils.remove(procInstId+"_sms");
        }
        redisUtils.set(procInstId+"_sms",nextWorkEmp);
        return variables;
    }
    @Override
    public String getType(){
        return "byGroup";
    }
}
