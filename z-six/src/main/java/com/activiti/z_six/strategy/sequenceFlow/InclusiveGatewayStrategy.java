package com.activiti.z_six.strategy.sequenceFlow;

import com.activiti.z_six.dto.NextNode;
import com.activiti.z_six.entity.process.FlowCond;
import com.activiti.z_six.mapper.processDefinitionMapper.FlowCondMapper;
import com.activiti.z_six.strategy.condExpression.CondExpressionContext;
import com.activiti.z_six.strategy.condExpression.CondExpressionStrategy;
import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import com.activiti.z_six.util.SystemConfig;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 发送给满足条件的多个任务节点
 */
@Component
public class InclusiveGatewayStrategy implements SequenceFlowStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Autowired
    private FlowCondMapper flowCondMapper;
    @Autowired
    private CondExpressionContext context;
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            , String procInstId){
        //获取相容网关分支对象
        List<SequenceFlow> inclusiveGateway=((InclusiveGateway) targetFlowElement).getOutgoingFlows();
        Boolean isHaveSelect=false;
        for(SequenceFlow sf:inclusiveGateway){
            //获取条件表达式
            String gateway=sf.getConditionExpression();
            if(SystemConfig.IsNullOrEmpty(gateway)){
                //获取处理人规则
                UserTask gateTask = (UserTask) sf.getTargetFlowElement();
                variables=sequenceFlowManager.getVariables(gateTask,variables,procInstId);
            }
            else {
                FlowCond flowCond=flowCondMapper.getFlowCond(sf.getId());
                if(flowCond.getExpression_type().equals("bySelect"))
                    isHaveSelect=true;
                CondExpressionStrategy condExpressionStrategy=context.getService(flowCond.getExpression_type());
                variables=condExpressionStrategy.analysis(variables,sf,targetFlowElement,flowCond,procInstId);
            }
        }
        //如果是手动选择方向，增加下一步要到达的节点
        if(isHaveSelect){
            List<NextNode> nextNodes=new ArrayList<>();
            for(SequenceFlow sf:inclusiveGateway){
                NextNode nextNode=new NextNode();
                UserTask gateTask=(UserTask)sf.getTargetFlowElement();
                nextNode.setId(gateTask.getId());
                nextNode.setName(gateTask.getName());
                nextNodes.add(nextNode);
            }
            variables.put("nextNode",nextNodes);
            variables.put("expType","InclusiveGateway");
        }
        return variables;
    }
    /**
     * 获取下一步节点
     * @param variables
     * @param targetFlowElement
     * @return
     */
    @Override
    public List<UserTask> getNextTasks(HashMap<String,Object> variables, FlowElement targetFlowElement){
        List<UserTask> userTaskList=new ArrayList<>();
        //获取相容网关分支对象
        List<SequenceFlow> inclusiveGateway=((InclusiveGateway) targetFlowElement).getOutgoingFlows();
        for(SequenceFlow sf:inclusiveGateway){
            //获取条件表达式
            String gateway=sf.getConditionExpression();
            if(SystemConfig.IsNullOrEmpty(gateway)){
                UserTask gateTask = (UserTask) sf.getTargetFlowElement();
                userTaskList.add(gateTask);
            }
            else {
                //使用JUEL表达式进行判断
                ExpressionFactory factory = new ExpressionFactoryImpl();
                SimpleContext context = new SimpleContext();
                //将参数写入JUEL表达式
                for (String key : variables.keySet()) {
                    context.setVariable(key, factory.createValueExpression(variables.get(key), String.class));
                }
                //执行判断，设置返回 boolean类型
                ValueExpression isPass = factory.createValueExpression(context, gateway, boolean.class);
                //如果是true，说明满足此分支路线
                if ((Boolean) isPass.getValue(context)) {
                    //转换成用户任务
                    UserTask gateTask = (UserTask) sf.getTargetFlowElement();
                    userTaskList.add(gateTask);
                }
            }
        }
        return userTaskList;
    }
    @Override
    public String getType(){
        return "InclusiveGateway";
    }
}
