package com.activiti.z_six.tenant.api;

import com.activiti.z_six.tenant.service.WorkFlowMessageContext;
import com.activiti.z_six.tenant.model.api.FlowAsk;
import com.activiti.z_six.util.ResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/api")
public class ProcessStatusFlowController {
    @Autowired
    private WorkFlowMessageContext workFlowMessageContext;

    /**
     * 获取自身消息
     * @param tenant 租户消息名称
     * @return 消息列表
     */
    @GetMapping("/flowMessage")
    public ResultRes contextLoad(String tenant){
        return ResultRes.success(workFlowMessageContext.getFlowMessage(false,tenant));
    }

    /**
     * 确认收到消息
     * @param flowAsk 消息验证码
     * @return 删除条数（用于核销消息）
     */
    @PostMapping("/ackByCode")
    public ResultRes contextLoad(@RequestBody FlowAsk flowAsk){
        System.out.println(flowAsk.toString());
        return ResultRes.success(workFlowMessageContext.receiveFlowMessage(flowAsk.getAckCode(), flowAsk.getTenant()));
    }
}
