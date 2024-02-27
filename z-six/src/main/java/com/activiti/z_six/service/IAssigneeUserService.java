package com.activiti.z_six.service;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public interface IAssigneeUserService {
    /**
    获取节点绑定的人员列表
     */
    HashMap<String,Object> getAssigneeUserWithTaskKey(String usertaskid,String ruleName);
    /***
     * 设置接收人
     * @return
     */
    String setAssigneeUser(JSONObject param);
    /**
    删除
     */
    String deleteAssigneeUser(String usertaskid, String username);

}
