package com.activiti.z_six.tenant.api;

import com.activiti.z_six.tenant.WorkFlowMessageContext;
import com.activiti.z_six.tenant.model.FlowAsk;
import com.activiti.z_six.util.ResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/api")
public class ProcessStatusFlowController {
    @Autowired
    private WorkFlowMessageContext workFlowMessageContext;
    @GetMapping("/flowMessage")
    public ResultRes contextLoad(String tenant){
        return ResultRes.success(workFlowMessageContext.getFlowMessage(false,tenant));
    }
    @PostMapping("/ackByCode")
    public ResultRes contextLoad(@RequestBody FlowAsk flowAsk){
        System.out.println(flowAsk.toString());
        return ResultRes.success(workFlowMessageContext.receiveFlowMessage(flowAsk.getAskCode(), flowAsk.getTenant()));
    }
}
