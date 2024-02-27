package com.activiti.z_six.service.impl;

import com.activiti.z_six.dto.controllerParams.ConditionDto;
import com.activiti.z_six.entity.process.FlowCond;
import com.activiti.z_six.mapper.processDefinitionMapper.FlowCondMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IFlowCondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class IFlowCondServiceImpl implements IFlowCondService {
    @Autowired
    private FlowCondMapper flowCondMapper;
    @Autowired
    private RedisUtils redisUtils;
    /**
     * 获取方向条件设置
     * @param id
     * @return
     */
    @Override
    public FlowCond getFlowCond(String id){
        return flowCondMapper.getFlowCond(id);
    }

    /**
     * 设置方向条件
     * @param conditionDto
     * @return
     */
    @Override
    public String setFlowCond(ConditionDto conditionDto){
        FlowCond flowCond=flowCondMapper.getFlowCond(conditionDto.getExpressionId());
        if(flowCond==null) {
            flowCond=new FlowCond();
            flowCond.setCondition_id(conditionDto.getExpressionId());
            flowCond.setExpression_type(conditionDto.getType());
            if (conditionDto.getType().equals("sql"))
                flowCond.setExpression_body(conditionDto.getSqlBody());
            if (conditionDto.getType().equals("webApi")) {
                flowCond.setExpression_body(conditionDto.getWebApiBody());
            }
            flowCond.setId(UUID.randomUUID().toString());
            flowCondMapper.inserCond(flowCond);
        }
        else{
            flowCond.setExpression_type(conditionDto.getType());
            if (conditionDto.getType().equals("sql"))
                flowCond.setExpression_body(conditionDto.getSqlBody());
            if (conditionDto.getType().equals("webApi")) {
                flowCond.setExpression_body(conditionDto.getWebApiBody());
            }
            flowCondMapper.updateCond(flowCond);
        }
        return "设置成功";
    }
    /**
     * 删除方向条件
     * @param conditionDto
     * @return
     */
    @Override
    public String deleteFlowCond(ConditionDto conditionDto){
        try{
            /**
             * 缓存中的也删除，防止发布时增加重复数据
             */
            if(redisUtils.exists(conditionDto.getExpressionId()))
                redisUtils.remove(conditionDto.getExpressionId());
            flowCondMapper.deleteCond(conditionDto.getExpressionId());
            return "删除成功";
        }
        catch (Exception ex){
            return "删除失败";
        }
    }
}
