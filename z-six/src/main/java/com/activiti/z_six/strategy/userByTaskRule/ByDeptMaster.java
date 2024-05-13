package com.activiti.z_six.strategy.userByTaskRule;

import com.activiti.z_six.entity.orgmanagement.DepartmentEntity;
import com.activiti.z_six.mapper.orgmanagementMapper.UserEntityMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @Author: 离狐千慕
 * @CreateTime: 2024-05-06
 * @Description: 策略：组长审核
 * @Version: 1.0
 */
@Component
public class ByDeptMaster implements UserByTaskRuleStrategy{
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
        variables.put(taskid + "_byDeptMaster", department.getManager());
        if(redisUtils.exists(procInstId+"_sms")){
            redisUtils.remove(procInstId+"_sms");
        }
        redisUtils.set(procInstId+"_sms",department.getManager());
        return variables;
    }

    @Override
    public String getType() {
        return "byDeptMaster";
    }
}
