package com.activiti.z_six.service;

import com.activiti.z_six.entity.taskAssignee.ApprovalTrack;

import java.io.InputStream;
import java.util.List;

public interface IApprovalTrackService {
    /**
     * 获取流程轨迹图
     * @param procInstId
     * @param processKey
     * @return
     * @throws Exception
     */
    InputStream getFlowImgByProcInstId(String procInstId, String processKey) throws Exception;
    /**
     * 获取审核记录列表
     * @param approvalTrack
     * @return
     */
    List<ApprovalTrack> approvalTracks(ApprovalTrack approvalTrack);

    /**
     * 获取运行时轨迹图
     * @param approvalTrack
     * @return
     */
    List<ApprovalTrack> getUserTaskTrack(ApprovalTrack approvalTrack);
}
