package com.activiti.z_six.tenant.impl;

import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.tenant.WorkFlowMessageContext;
import com.activiti.z_six.tenant.model.FlowMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 租户端可靠消息交付
 * 采用发送-确认的收付模式，租户端在收到消息以后，应该立即确认收到消息的条数并立即返回相关的key进行回放
 */
@Service
public class WorkFlowMessageRuntimeContext implements WorkFlowMessageContext {
    @Autowired
    private RedisUtils redisUtils;
    private final static String DEFAULT_TENANT="main";
    private final static String STATUS_DRIVER_CONTEXT="status:driver:tenants:";
    @Override
    public void StorageMessageByTenant(FlowMessage flowMessage,String tenant) {
//        如果租户为空，则设置租户为主租户
        if (tenant==null || tenant.equals("")) tenant=DEFAULT_TENANT;
//        生成随机确认码
        String askCode = UUID.randomUUID().toString();
        flowMessage.setAckCode(askCode);
//        存库
        redisUtils.hmSet(STATUS_DRIVER_CONTEXT+tenant,askCode, JSON.toJSONString(flowMessage));

        System.out.println("存储内容");
//        List<FlowMessage> message = getFlowMessage(false, tenant);
//        message.forEach(System.out::println);
    }

    @Override
    public List<FlowMessage> getFlowMessage(Boolean autoMode, String tenant) {
//        获取到所有数据
        Object allData = redisUtils.get(STATUS_DRIVER_CONTEXT + ((tenant==null || tenant.equals(""))?DEFAULT_TENANT:tenant));
//        转为map
        Map<String, String> map = (Map<String, String>) allData;
//        开始循环获取数据
        List<FlowMessage> flowMessageList=new LinkedList<>();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            flowMessageList.add(JSONObject.parseObject(next.getValue(), FlowMessage.class));
        }
        return flowMessageList;
    }

    @Override
    public Long receiveFlowMessageList(List<String> askList, String tenant) {
        //        如果租户为空，则设置租户为主租户
        if (tenant==null || tenant.equals("")) tenant=DEFAULT_TENANT;
        return redisUtils.delCacheMapValue(STATUS_DRIVER_CONTEXT+tenant,askList);
    }

    @Override
    public Long receiveFlowMessage(String askCode, String tenant) {
        return receiveFlowMessageList(Collections.singletonList(askCode),tenant);
    }
}
