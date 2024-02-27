package com.activiti.z_six.mapper.processMapper;

import com.activiti.z_six.entity.process.FlowEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FlowEntityMapper {
    /**
    更新流程信息
     */
    int updateFlowEntity(FlowEntity flowEntity);

    /**
     * 获取流程发起列表
     * @param flowSort
     * @return
     */
    List<FlowEntity> getFlowListBySort(String flowSort);

    /**
     * 获取常用的流程
     * @param username
     * @return
     */
    List<FlowEntity> getCommProceList(@Param("username") String username);
}
