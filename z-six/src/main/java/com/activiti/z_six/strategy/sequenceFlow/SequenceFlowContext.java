package com.activiti.z_six.strategy.sequenceFlow;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SequenceFlowContext {
    /**
     * 流转方向策略对象
     */
    private final List<SequenceFlowStrategy> sequenceFlowStrategies;
    /**
     * 构造函数
     * @param sequenceFlowStrategies
     */
    public SequenceFlowContext(List<SequenceFlowStrategy> sequenceFlowStrategies){
        this.sequenceFlowStrategies=sequenceFlowStrategies;
    }

    /**
     * server容器
     */
    private Map<String, SequenceFlowStrategy> stringSequenceFlowStrategyMap=new HashMap<>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        stringSequenceFlowStrategyMap=sequenceFlowStrategies.stream()
                .collect(Collectors.toMap(SequenceFlowStrategy::getType, o->o));
    }

    /**
     * 获取服务对象
     * @param serviceType
     * @return
     */
    public SequenceFlowStrategy getService(String serviceType){
        return stringSequenceFlowStrategyMap.get(serviceType);
    }
}
