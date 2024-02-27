package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.ReturnWorkEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface ReturnWorkMapper {
    /**
     * 增加退回记录
     * @param returnWorkEntity
     * @return
     */
    int createReturnDetailed(ReturnWorkEntity returnWorkEntity);
    List<ReturnWorkEntity> returnWorkList(String username);

    /**
     * 删除退回记录
     * @param id
     * @return
     */
    int deleteReturn(String id);

    /**
     * 获取退回信息
     * @param taskid
     * @return
     */
    ReturnWorkEntity getReturnWork(String taskid);
}
