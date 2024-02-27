package com.activiti.z_six.strategy.condExpression;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CondExpressionContext {
    /**
     * 流转方向策略对象
     */
    private final List<CondExpressionStrategy> condExpressionStrategies;
    /**
     * 构造函数
     * @param condExpressionStrategies
     */
    public CondExpressionContext(List<CondExpressionStrategy> condExpressionStrategies){
        this.condExpressionStrategies=condExpressionStrategies;
    }

    /**
     * server容器
     */
    private Map<String, CondExpressionStrategy> condExpressionStrategyMap=new HashMap<>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        condExpressionStrategyMap=condExpressionStrategies.stream()
                .collect(Collectors.toMap(CondExpressionStrategy::getType, o->o));
    }

    /**
     * 获取服务对象
     * @param serviceType
     * @return
     */
    public CondExpressionStrategy getService(String serviceType){
        return condExpressionStrategyMap.get(serviceType);
    }
}
