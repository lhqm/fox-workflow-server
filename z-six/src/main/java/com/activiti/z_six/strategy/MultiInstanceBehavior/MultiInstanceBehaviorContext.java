package com.activiti.z_six.strategy.MultiInstanceBehavior;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MultiInstanceBehaviorContext {
    /**
     * 处理人规则策略对象
     */
    private final List<MultiInstanceBehaviorStrategy> multiInstanceBehaviorStrategies;
    /**
     * 构造函数
     * @param multiInstanceBehaviorStrategies
     */
    public MultiInstanceBehaviorContext(List<MultiInstanceBehaviorStrategy> multiInstanceBehaviorStrategies){
        this.multiInstanceBehaviorStrategies=multiInstanceBehaviorStrategies;
    }

    /**
     * server容器
     */
    private Map<String, MultiInstanceBehaviorStrategy> stringSequenceFlowStrategyMap=new HashMap<>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        stringSequenceFlowStrategyMap=multiInstanceBehaviorStrategies.stream()
                .collect(Collectors.toMap(MultiInstanceBehaviorStrategy::getType, o->o));
    }

    /**
     * 获取服务对象
     * @param serviceType
     * @return
     */
    public MultiInstanceBehaviorStrategy getService(String serviceType){
        return stringSequenceFlowStrategyMap.get(serviceType);
    }
}
