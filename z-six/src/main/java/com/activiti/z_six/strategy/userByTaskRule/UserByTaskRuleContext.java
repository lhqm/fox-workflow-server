package com.activiti.z_six.strategy.userByTaskRule;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserByTaskRuleContext {
    /**
     * 获取人员策略对象
     */
    private final List<UserByTaskRuleStrategy> userByTaskRuleStrategies;
    /**
     * 构造函数
     * @param userByTaskRuleStrategies
     */
    public UserByTaskRuleContext(List<UserByTaskRuleStrategy> userByTaskRuleStrategies){
        this.userByTaskRuleStrategies=userByTaskRuleStrategies;
    }

    /**
     * server容器
     */
    private Map<String, UserByTaskRuleStrategy> userByTaskRuleStrategyMap=new HashMap<>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        userByTaskRuleStrategyMap=userByTaskRuleStrategies.stream()
                .collect(Collectors.toMap(UserByTaskRuleStrategy::getType, o->o));
    }

    /**
     * 获取服务对象
     * @param serviceType
     * @return
     */
    public UserByTaskRuleStrategy getService(String serviceType){
        return userByTaskRuleStrategyMap.get(serviceType);
    }
}
