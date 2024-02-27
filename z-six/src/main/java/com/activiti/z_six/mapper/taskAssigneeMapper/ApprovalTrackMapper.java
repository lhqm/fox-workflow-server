package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.ApprovalTrack;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApprovalTrackMapper {
    /**
     * 增加审核意见
     */
    int addApprovalTrack(ApprovalTrack approvalTrack);
    /**
     * 获取审核列表
     */
    List<ApprovalTrack> approvalTracks(String process_ints_id);

    /**
     * 获取审核轨迹
     * @param process_ints_id
     * @return
     */
    List<ApprovalTrack> getUserTaskTrack(String process_ints_id);
}
