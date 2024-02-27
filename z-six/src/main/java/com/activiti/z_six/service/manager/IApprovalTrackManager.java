package com.activiti.z_six.service.manager;

import com.activiti.z_six.entity.taskAssignee.ApprovalTrack;
import com.activiti.z_six.mapper.taskAssigneeMapper.ApprovalTrackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IApprovalTrackManager {
    @Autowired
    private ApprovalTrackMapper approvalTrackMapper;

    /**
     * 获取流程审核记录表
     * @param process_ints_id
     * @return
     */
    public List<ApprovalTrack> getApprovalTracks(String process_ints_id){
        return approvalTrackMapper.approvalTracks(process_ints_id);
    }
    public List<ApprovalTrack> getUserTaskTrack(String process_ints_id){
        return approvalTrackMapper.getUserTaskTrack(process_ints_id);
    }
    /**
     *  通过流程实例ID获取历史流程实例
     *
     * @param processInstanceId 流程实例Id
     * @return 历史流程实例
     */
    public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<HistoricProcessInstance> historicProcessInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        return historicProcessInstance.get(0);
    }
    /**
     * 通过流程实例ID获取流程中已经执行的结点，按照执行先后顺序排序
     *
     * @param processInstanceId 流程实例Id
     * @return 已经执行的节点
     */
    public List<HistoricActivityInstance> getHistoricActivityInstancesAsc(String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        return processEngine.getHistoryService().createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceId()
                .asc()
                .list();
    }
    /**
     * 通过流程实例ID获取流程中正在执行的节点(只需要在历史表中寻找是否有过该流程实例即可)
     * (因为画图时可能已经走完所有的流程，此时已无还需执行的流程实例，只有历史表中才会有)
     *
     * @param processInstanceId 流程实例ID
     * @return 正在执行的节点
     */
    public List<Execution> getRunningActivityInstance(String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        return processEngine.getRuntimeService().createExecutionQuery()
                .processInstanceId(processInstanceId)
                .list();
    }
}
