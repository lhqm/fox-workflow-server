package com.activiti.z_six.strategy.dict;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DictContext {
    /**
     * 字典策略对象
     */
    private final List<DictStrategy> dictStrategyList;
    /**
     * 构造函数
     * @param dictStrategyList
     */
    public DictContext(List<DictStrategy> dictStrategyList){
        this.dictStrategyList=dictStrategyList;
    }
    /**
     * server容器
     */
    private Map<String, DictStrategy> dictStrategyMap = new HashMap<>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        dictStrategyMap=dictStrategyList.stream()
                .collect(Collectors.toMap(DictStrategy::getType, o -> o));
    }
    /**
     * 获取方法
     * @param serviceType
     * @return
     */
    public DictStrategy getService(String serviceType){
        return dictStrategyMap.get(serviceType);
    }
}
