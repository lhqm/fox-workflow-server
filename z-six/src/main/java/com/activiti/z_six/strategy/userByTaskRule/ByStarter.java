package com.activiti.z_six.strategy.userByTaskRule;

import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.mapper.orgmanagementMapper.UserEntityMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ByStarter implements UserByTaskRuleStrategy{
    @Autowired
    private UserEntityMapper userEntityMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public HashMap<String, Object> getUserByTaskRule(String taskid, String ruleName, HashMap<String, Object> variables, String procInstId) {
        HistoricProcessInstance instance = ProcessEngines.
                getDefaultProcessEngine().getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(procInstId).singleResult();
        String nextWorkEmp="";
        if (instance!=null){
            String userId = instance.getStartUserId();
            UserEntity userEntity=userEntityMapper.getUserByUserId(userId);
            nextWorkEmp= userEntity.getUsername();
        }
        //放入缓存，用于发送消息推送
        variables.put(taskid + "_byStarter", nextWorkEmp);
        if(redisUtils.exists(procInstId+"_sms")){
            redisUtils.remove(procInstId+"_sms");
        }
        redisUtils.set(procInstId+"_sms",nextWorkEmp);
        return variables;
    }

    @Override
    public String getType() {
        return "byStarter";
    }
}
