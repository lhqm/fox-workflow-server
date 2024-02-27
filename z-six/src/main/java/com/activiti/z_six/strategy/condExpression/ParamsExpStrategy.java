package com.activiti.z_six.strategy.condExpression;

import com.activiti.z_six.entity.process.FlowCond;
import com.activiti.z_six.entity.taskAssignee.GenerWork;
import com.activiti.z_six.mapper.taskAssigneeMapper.GenerWorkMapper;
import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import com.alibaba.fastjson.JSONObject;
import com.sun.el.lang.ExpressionBuilder;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.TreeValueExpression;
import de.odysseus.el.tree.ExpressionNode;
import de.odysseus.el.tree.Node;
import de.odysseus.el.tree.impl.ast.AstBinary;
import de.odysseus.el.tree.impl.ast.AstEval;
import de.odysseus.el.tree.impl.ast.AstIdentifier;
import de.odysseus.el.util.SimpleContext;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 根据参数判断方向条件
 */
@Component
public class ParamsExpStrategy implements CondExpressionStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Autowired
    private GenerWorkMapper generWorkMapper;
    @Override
    public HashMap<String,Object> analysis(HashMap<String,Object> variables,
                                           SequenceFlow sequenceFlow,
                                           FlowElement targetFlowElement,
                                           FlowCond flowCond,String procInstId){
        //获取条件表达式
        String gateway=sequenceFlow.getConditionExpression();
        //使用JUEL表达式进行判断
        ExpressionFactory factory=new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();

        //获取当前登陆人信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        //获取表单数据作为参数
        GenerWork generWork=generWorkMapper.getGenerWorkByInst(procInstId);
        Map<String,Object> params=(Map) JSONObject.parseObject(generWork.getData_json());
        params.put("proce_inst_id",procInstId);
        params.put("user_name",username);
        //将表单参数写入JUEL表达式
        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            context.setVariable(entry.getKey(), factory.createValueExpression(entry.getValue(), String.class));
        }
        //将自定义参数写入JUEL表达式
        for(String key:variables.keySet()){
            context.setVariable(key, factory.createValueExpression(variables.get(key), String.class));
        }
        //执行判断，设置返回 boolean类型
        ValueExpression isPass = factory.createValueExpression(context, gateway, boolean.class);
        //如果是true，说明满足此分支路线
        if((Boolean)isPass.getValue(context)){
            //将表达式的参数，放入
            Integer childrenNum=ExpressionBuilder.createNode(gateway).jjtGetNumChildren();
            for(int i=0;i<childrenNum;i++){
                for(String key:params.keySet()){
                    if(key.equals(ExpressionBuilder.createNode(gateway).jjtGetChild(i).getImage())){
                        variables.put(key, params.get(key));
                    }
                }
            }
            //转换成用户任务对象
            UserTask gateTask=(UserTask)sequenceFlow.getTargetFlowElement();
            variables=sequenceFlowManager.getVariables(gateTask,variables,procInstId);
        }
        return variables;
    }
    @Override
    public String getType(){
        return "params";
    }
}
