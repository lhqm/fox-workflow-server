package com.activiti.z_six.service;

import com.activiti.z_six.dto.controllerParams.ConditionDto;
import com.activiti.z_six.entity.process.FlowCond;
import org.apache.ibatis.annotations.Param;

public interface IFlowCondService {
    /**
     * 获取方向条件设置
     * @param id
     * @return
     */
    FlowCond getFlowCond(String id);

    /**
     * 设置方向条件
     * @param conditionDto
     */
    String setFlowCond(ConditionDto conditionDto);

    /**
     * 删除方向条件
     * @param conditionDto
     * @return
     */
    String deleteFlowCond(ConditionDto conditionDto);
}
