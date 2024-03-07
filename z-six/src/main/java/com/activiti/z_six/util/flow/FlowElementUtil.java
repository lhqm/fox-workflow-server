package com.activiti.z_six.util.flow;

import com.activiti.z_six.entity.tenant.FlowProcess;
import com.activiti.z_six.strategy.flowConvert.FlowElementStrategyExecutor;
import org.activiti.bpmn.model.FlowElement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FlowElementUtil {
    private static final FlowElementStrategyExecutor executor;

    /**
     * 工具类加载初始化时，注册策略类
     */
    static {
        executor = new FlowElementStrategyExecutor();
    }
    /**
     * 获取流程定义图（注意，是图，存在多级的巡向和父子关系，一个节点可能有好几个父子关系）
     * 可能存在：
     * 1.顺序流程
     * 2.排他网关并线
     * 3.子流程
     * 4.嵌套子流程
     * 5.自子流程内连续越级进入主流程的现象
     * @param flowElements 流程顶层节点
     * @return 流程数据
     */
    public static List<FlowProcess> getFlowDefinitionMap(Collection<FlowElement> flowElements){
        List<FlowProcess> processes=new LinkedList<>();
//        循环element,找到流程定义
        for (FlowElement element : flowElements) {
            processes.add(getFlowProcessByFlowElement(element));
        }
        return processes;
    }



    private static FlowProcess getFlowProcessByFlowElement(FlowElement flowElement){
//        获取流程
        FlowProcess flowProcess = executor.executeStrategy(flowElement);
//        为没有名字的节点加上默认名字
        if (flowProcess.getFlowName()==null) flowProcess.setFlowName("未命名流程节点");
        return flowProcess;
    }
}
