package com.activiti.z_six.strategy.sequenceFlow;
import com.activiti.z_six.dto.NextNode;
import com.activiti.z_six.dto.SendActionDto;
import com.activiti.z_six.entity.process.FlowCond;
import com.activiti.z_six.mapper.processDefinitionMapper.FlowCondMapper;
import com.activiti.z_six.strategy.condExpression.CondExpressionContext;
import com.activiti.z_six.strategy.condExpression.CondExpressionStrategy;
import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
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
 * 条件分支，根据条件判断下一步的任务节点
 */
@Component
public class ExclusiveGatewayStrategy implements SequenceFlowStrategy{
    @Autowired
    private FlowCondMapper flowCondMapper;
    @Autowired
    private CondExpressionContext context;

    /**
     * 获取节点处理人
     * @param variables
     * @param targetFlowElement
     * @param procInstId
     * @return
     */
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            ,String procInstId){
        //获取排他网关分支对象
        List<SequenceFlow> exclusiveGateway=((ExclusiveGateway) targetFlowElement).getOutgoingFlows();
        Boolean isHaveSelect=false;
        //循环判断条件
        for(SequenceFlow sf:exclusiveGateway){
            FlowCond flowCond=flowCondMapper.getFlowCond(sf.getId());
            if(flowCond.getExpression_type().equals("bySelect")){
                isHaveSelect=true;
                variables.put(sf.getId(),0);
            }
            CondExpressionStrategy condExpressionStrategy=context.getService(flowCond.getExpression_type());
            variables=condExpressionStrategy.analysis(variables,sf,targetFlowElement,flowCond,procInstId);
        }
        //如果是手动选择方向，增加下一步要到达的节点
        if(isHaveSelect){
            List<NextNode> nextNodes=new ArrayList<>();
            for(SequenceFlow sf:exclusiveGateway){
                NextNode nextNode=new NextNode();
                UserTask gateTask=(UserTask)sf.getTargetFlowElement();
                nextNode.setId(gateTask.getId());
                nextNode.setName(gateTask.getName());
                nextNodes.add(nextNode);
            }
            variables.put("nextNode",nextNodes);
            variables.put("expType","ExclusiveGateway");
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
        //获取排他网关分支对象
        List<SequenceFlow> exclusiveGateway=((ExclusiveGateway) targetFlowElement).getOutgoingFlows();
        //循环判断条件
        for(SequenceFlow sf:exclusiveGateway){
            //获取条件表达式
            String gateway=sf.getConditionExpression();
            //使用JUEL表达式进行判断
            ExpressionFactory factory=new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            //将参数写入JUEL表达式
            for(String key:variables.keySet()){
                context.setVariable(key, factory.createValueExpression(variables.get(key), String.class));
            }
            //执行判断，设置返回 boolean类型
            ValueExpression isPass = factory.createValueExpression(context, gateway, boolean.class);
            //如果是true，说明满足此分支路线
            if((Boolean)isPass.getValue(context)){
                //转换成用户任务
                UserTask gateTask=(UserTask)sf.getTargetFlowElement();
                userTaskList.add(gateTask);
            }
        }
        return userTaskList;
    }
    @Override
    public String getType(){
        return "ExclusiveGateway";
    }
}
