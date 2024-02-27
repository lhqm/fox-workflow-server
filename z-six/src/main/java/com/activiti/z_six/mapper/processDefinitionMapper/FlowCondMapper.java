package com.activiti.z_six.mapper.processDefinitionMapper;

import com.activiti.z_six.entity.process.FlowCond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FlowCondMapper {
    /**
     * 获取方向条件设置
     * @param id
     * @return
     */
    FlowCond getFlowCond(@Param("id") String id);

    /**
     * 增加方向条件设置
     * @param flowCond
     * @return
     */
    int inserCond(FlowCond flowCond);

    /**
     * 更新方向条件设置
     * @param flowCond
     * @return
     */
    int updateCond(FlowCond flowCond);

    /**
     * 删除方向条件设置
     * @param id
     * @return
     */
    int deleteCond(@Param("id") String id);
}
