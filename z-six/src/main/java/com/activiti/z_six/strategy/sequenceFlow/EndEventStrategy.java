package com.activiti.z_six.strategy.sequenceFlow;

import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.strategy.manager.SequenceFlowManager;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 结束节点
 */
@Component
public class EndEventStrategy implements SequenceFlowStrategy{
    @Autowired
    private SequenceFlowManager sequenceFlowManager;
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public HashMap<String,Object> getNextUsers(HashMap<String,Object> variables, FlowElement targetFlowElement
            , String procInstId){
        try {
            //获取子流程对象
            SubProcess subProcess = (SubProcess) targetFlowElement.getParentContainer();
            List<SequenceFlow> subOutgoingFlows = subProcess.getOutgoingFlows();
            //结束后面还有连接线，说明这是子流程结束，需要获取父流程的下一步节点信息
            if (subOutgoingFlows.size() > 0) {
                //获取任务对象
                UserTask subTask = (UserTask) subOutgoingFlows.get(0).getTargetFlowElement();
                variables=sequenceFlowManager.getVariables(subTask,variables,procInstId);
            }
            return variables;
        }//报错说明这是普通流程的结束
        catch (Exception e) {
            if(redisUtils.exists(procInstId+"_end")){
                redisUtils.remove(procInstId+"_end");
            }
//            这段代码在前置修改后会报空指针。正确做法应该是通过流程实例查到发起人，然后把发起人塞到这个_end里边
            redisUtils.set(procInstId+"_sms","流程已结束");
            return variables;
        }
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
        try {
            //获取子流程对象
            SubProcess subProcess = (SubProcess) targetFlowElement.getParentContainer();
            List<SequenceFlow> subOutgoingFlows = subProcess.getOutgoingFlows();
            //结束后面还有连接线，说明这是子流程结束，需要获取父流程的下一步节点信息
            if (subOutgoingFlows.size() > 0) {
                UserTask subTask = (UserTask) subOutgoingFlows.get(0).getTargetFlowElement();
                userTaskList.add(subTask);
            }
        }//报错说明这是普通流程的结束
        catch (Exception e) {
            UserTask endUserTask=new UserTask();
            endUserTask.setId("endEvent");
            endUserTask.setName("结束");
            userTaskList.add(endUserTask);
        }
        return userTaskList;
    }
    @Override
    public String getType(){
        return "EndEvent";
    }
}
