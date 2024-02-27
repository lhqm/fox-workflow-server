package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.ReturnWayEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ReturnWayMapper {
    int createReturnWay(ReturnWayEntity returnWayEntity);
    /**
     * 获取退回方式
     * @param task_def_id
     * @return
     */
    ReturnWayEntity getReturnWay(String task_def_id);
}
