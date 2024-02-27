package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.TaskInstEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TaskInstEntityMapper {
    /**
     增加轨迹
     */
    int addTaskInstTrack(TaskInstEntity taskInstEntity);
    /**
     删除轨迹
     */
    int deleteInstTrack(String id);
}
