package com.activiti.z_six.service.impl;

import com.activiti.z_six.entity.taskAssignee.ApprovalTrack;
import com.activiti.z_six.service.IApprovalTrackService;
import com.activiti.z_six.service.manager.IApprovalTrackManager;
import com.activiti.z_six.service.manager.IProcessTrackManager;
import com.activiti.z_six.util.SystemConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class IApprovalTrackServiceImpl implements IApprovalTrackService {
    private final IProcessTrackManager processTrackManager;
    private final IApprovalTrackManager approvalTrackManager;

    /**
     * 获取轨迹图/流程图
     * @param procInstId
     * @param processKey
     * @return
     * @throws Exception
     */
    @Override
    public InputStream getFlowImgByProcInstId(String procInstId,String processKey) throws Exception {
        if(!SystemConfig.IsNullOrEmpty(procInstId))
            return processTrackManager.getFlowImgByProcInstId(procInstId);
        else{
            return processTrackManager.getFlowImgByPorcessKey(processKey);
        }
    }

    /**
     * 获取审核记录列表
     * @param approvalTrack
     * @return
     */
    public List<ApprovalTrack> approvalTracks(ApprovalTrack approvalTrack){
        return approvalTrackManager.getApprovalTracks(approvalTrack.getProcess_ints_id());
    }

    /**
     * 获取用户轨迹
     * @param approvalTrack
     * @return
     */
    public List<ApprovalTrack> getUserTaskTrack(ApprovalTrack approvalTrack){
        return approvalTrackManager.getUserTaskTrack(approvalTrack.getProcess_ints_id());
    }
}
