package com.activiti.z_six.strategy.userByTaskRule;

import com.activiti.z_six.entity.orgmanagement.DepartmentEntity;
import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.entity.taskAssignee.AssigneeUserEntity;
import com.activiti.z_six.mapper.orgmanagementMapper.UserEntityMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import com.activiti.z_six.util.StringUtils;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: 离狐千慕
 * @CreateTime: 2024-04-29
 * @Description: 策略：分管领导审核
 * @Version: 1.0
 */

@Component
public class ByDeptLeader implements UserByTaskRuleStrategy{
    @Autowired
    private UserEntityMapper userEntityMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public HashMap<String, Object> getUserByTaskRule(String taskid, String ruleName, HashMap<String, Object> variables, String procInstId) {
//        获取任务
        HistoricProcessInstance instance = ProcessEngines.
                getDefaultProcessEngine().getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(procInstId).singleResult();
//        找到负责人和部门
            String userId = instance.getStartUserId();
        DepartmentEntity department = userEntityMapper.getDepartmentByUserId(userId);

        //放入缓存，用于发送消息推送
        variables.put(taskid + "_byDeptLeader", department.getLeader());
        if(redisUtils.exists(procInstId+"_sms")){
            redisUtils.remove(procInstId+"_sms");
        }
        redisUtils.set(procInstId+"_sms",department.getLeader());
        return variables;
    }

    @Override
    public String getType() {
        return "byDeptLeader";
    }
}
