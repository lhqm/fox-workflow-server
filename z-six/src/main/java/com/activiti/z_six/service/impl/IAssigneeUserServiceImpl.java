package com.activiti.z_six.service.impl;

import com.activiti.z_six.entity.taskAssignee.AssigneeDSREntity;
import com.activiti.z_six.entity.taskAssignee.AssigneeUserEntity;
import com.activiti.z_six.mapper.taskAssigneeMapper.AssigneeUserMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IAssigneeUserService;
import com.activiti.z_six.service.manager.IProcessDefinitionManager;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IAssigneeUserServiceImpl implements IAssigneeUserService {
    @Autowired
    private AssigneeUserMapper assigneeUserMapper;
    @Autowired
    private IProcessDefinitionManager processDefManager;
    @Autowired
    private RedisUtils redisUtils;
    private Long expireTime=Long.parseLong("360000") ;
    @Override
    public HashMap<String,Object> getAssigneeUserWithTaskKey(String usertaskid,String ruleName){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<AssigneeUserEntity> assigneeUserEntityList;
        List<AssigneeDSREntity> assigneeDSREntities;
        String assigneeString ="";
        String assignessdsrStr="";
        if(redisUtils.exists(usertaskid+"_"+ruleName)){
            assigneeString=redisUtils.get(usertaskid+"_"+ruleName).toString();
        }
        //如果redis中不存在
        if (SystemConfig.IsNullOrEmpty(assigneeString)||assigneeString.equals("[]")) {
            assigneeUserEntityList=assigneeUserMapper.getAssigneeUserWithTaskKey(usertaskid,ruleName);
        }
        else {
            assigneeUserEntityList=JSONObject.parseArray(assigneeString, AssigneeUserEntity.class);
            if(ruleName.equals("byUser")||ruleName.equals("byDept")) {
                if (SystemConfig.IsNullOrEmpty(assigneeUserEntityList.get(0).getUsername())
                        && SystemConfig.IsNullOrEmpty(assigneeUserEntityList.get(0).getName())) {
                    assigneeUserEntityList = assigneeUserMapper.getAssigneeUserWithTaskKey(usertaskid, ruleName);
                }
            }
        }
        hashMap.put("assigneeUserEntityList",assigneeUserEntityList);
        if (SystemConfig.IsNullOrEmpty(assignessdsrStr)||assigneeString.equals("[]")) {
            assigneeDSREntities=assigneeUserMapper.getAssigneeDSR(usertaskid);
        }
        else {
            assigneeDSREntities=JSONObject.parseArray(assignessdsrStr, AssigneeDSREntity.class);
            if(SystemConfig.IsNullOrEmpty(assigneeDSREntities.get(0).getId())){
                assigneeDSREntities=assigneeUserMapper.getAssigneeDSR(usertaskid);
            }
        }
        hashMap.put("assigneeDSREntities",assigneeDSREntities);

        return hashMap;
    }

    /***
     * 设置接收人
     * @return
     */
    @Override
    public String setAssigneeUser(JSONObject param){
        //人员数据
        JSONArray userlist = param.getJSONArray("userlist");
        //规则节点id标识
        String usertaskid=param.getString("usertaskid");
        //节点id
        String id=param.getString("id");
        //规则id
        String ruleName=param.getString("ruleName");
        //如果redis中已经存在，先删除
        if(redisUtils.exists(usertaskid)){
            redisUtils.remove(usertaskid);
        }
        //写入redis
        redisUtils.set(usertaskid,userlist.toString(),expireTime);

        List<AssigneeUserEntity> assigneeUserEntityList=assigneeUserMapper.getAssigneeUserWithTaskKey(id,ruleName);
        if(assigneeUserEntityList.size()>0){
            processDefManager.deleteAssigneeUserByTaskid(id);
            processDefManager.setAssigneeUser(userlist.toString(), id,ruleName);
            return "设置成功";
        }
        else
            return "设置成功";
    }
    @Override
    public String deleteAssigneeUser(String usertaskid, String username){
        AssigneeUserEntity assigneeUserEntity=new AssigneeUserEntity();
        assigneeUserEntity.setUsertaskid(usertaskid);
        assigneeUserEntity.setUsername(username);
        assigneeUserMapper.deleteAssigneeUser(assigneeUserEntity);

        return "执行成功";
    }
}
