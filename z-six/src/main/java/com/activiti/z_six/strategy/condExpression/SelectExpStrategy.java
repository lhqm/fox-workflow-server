package com.activiti.z_six.strategy.condExpression;

import com.activiti.z_six.entity.process.FlowCond;
import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import com.activiti.z_six.util.SystemConfig;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 用户手动选择方向
 */
@Component
public class SelectExpStrategy implements CondExpressionStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Override
    public HashMap<String,Object> analysis(HashMap<String,Object> variables,
                                           SequenceFlow sequenceFlow,
                                           FlowElement targetFlowElement,
                                           FlowCond flowCond, String procInstId){
        //第一次进来，没有选择节点，返回状态
        if(SystemConfig.IsNullOrEmpty(variables.get("nextNode")))
            variables.put("condSelect", "bySelect");
        else{
            String nextNode=variables.get("nextNode").toString();
            String targetRef=sequenceFlow.getTargetRef();
            //选择了多个节点
            if(nextNode.contains(","))
            {
                String[] nodes=nextNode.split(",");
                for(String node:nodes){
                    //如果为空，返回
                    if (SystemConfig.IsNullOrEmpty(node))
                        continue;
                    //如果包含，通过
                    if(node.equals(targetRef))
                        variables.put(sequenceFlow.getId(),1);
                    else{
                        //如果本次不包含，判断上几个循环中是否增加了
                        try{
                            if(!SystemConfig.IsNullOrEmpty(variables.get(node).toString()))
                                continue;
                        }catch (Exception ex){
                            //将没有增加的，增加上
                            variables.put(sequenceFlow.getId(),0);
                        }
                    }
                    //查找节点处理人
                    UserTask gateTask=(UserTask)sequenceFlow.getTargetFlowElement();
                    variables=sequenceFlowManager.getVariables(gateTask,variables,procInstId);
                }
            }
            //只选择了一个节点
            else{
                if(nextNode.equals(targetRef)){
                    variables.remove(sequenceFlow.getId());
                    variables.put(sequenceFlow.getId(),1);
                }
                //查找节点处理人
                UserTask gateTask=(UserTask)sequenceFlow.getTargetFlowElement();
                variables=sequenceFlowManager.getVariables(gateTask,variables,procInstId);
            }
        }
        return variables;
    }
    @Override
    public String getType(){
        return "bySelect";
    }
}
