package com.activiti.z_six.service.manager;

import com.activiti.z_six.entity.taskAssignee.OvProcessInstance;
import com.activiti.z_six.mapper.taskAssigneeMapper.OvProcessInstanceMapper;
import com.activiti.z_six.util.trackUtil.CustomProcessDiagramGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class IProcessTrackManager {
    private final IApprovalTrackManager approvalTrackManager;
    @Autowired //流程实例服务
    private final OvProcessInstanceMapper ovProcessInstanceMapper;
    @Autowired //历史任务服务
    private final RepositoryService repositoryService;

    /**
     * 根据流程编号，获取流程图
     * @param processKey
     * @return
     * @throws Exception
     */
    public InputStream getFlowImgByPorcessKey(String processKey) throws Exception{
        InputStream imageStream = null;
        try{
            OvProcessInstance ovProcessInstance=ovProcessInstanceMapper.getProcessInsLastVersion(processKey);
            BpmnModel bpmnModel = repositoryService.getBpmnModel(ovProcessInstance.getId_());

            // 定义流程画布生成器
            CustomProcessDiagramGenerator processDiagramGenerator = new CustomProcessDiagramGenerator();
            imageStream = processDiagramGenerator.generateDiagramCustom(
                    bpmnModel,new ArrayList<>(),new ArrayList<>(),
                    "宋体", "微软雅黑", "黑体");
            return imageStream;
        }
        catch (Exception e) {
            log.error("获取流程图时出现异常！", e.getMessage());
            throw new Exception("通过流程processKey" + processKey + "获取流程图时出现异常！", e);
        } finally {
            if (imageStream != null) {
                imageStream.close();
            }
        }
    }

    /**
     * 根据proc_inst_id获取轨迹
     * @param procInstId
     * @return
     * @throws Exception
     */
    public InputStream getFlowImgByProcInstId(String procInstId) throws Exception {
        InputStream imageStream = null;
        try {
            // 通过流程实例ID获取历史流程实例
            HistoricProcessInstance historicProcessInstance = approvalTrackManager.getHistoricProcessInstance(procInstId);

            // 通过流程实例ID获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = approvalTrackManager.getHistoricActivityInstancesAsc(procInstId);

            // 将已经执行的节点放入高亮显示节点集合
            List<String> highLightedActivityIdList = historicActivityInstanceList.stream()
                    .map(HistoricActivityInstance::getActivityId)
                    .collect(Collectors.toList());

            // 通过流程实例ID获取流程中正在执行的节点
            List<Execution> runningActivityInstanceList = approvalTrackManager.getRunningActivityInstance(procInstId);
            List<String> runningActivityIdList = new ArrayList<>();
            for (Execution execution : runningActivityInstanceList) {
                if (!StringUtils.isEmpty(execution.getActivityId())) {
                    runningActivityIdList.add(execution.getActivityId());
                }
            }

            // 定义流程画布生成器
            CustomProcessDiagramGenerator processDiagramGenerator = new CustomProcessDiagramGenerator();

            // 获取流程定义Model对象
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

            // 获取已经流经的流程线，需要高亮显示流程已经发生流转的线id集合
            List<String> highLightedFlowsIds = getHighLightedFlowsByIncomingFlows(bpmnModel, historicActivityInstanceList);

            // 获取活动节点
            List<String> runningActivityFlowsIds = getRunningActivityFlowIdsByIcommingFlows(bpmnModel, runningActivityIdList, historicActivityInstanceList);

            // 使用默认配置获得流程图表生成器，并生成追踪图片字符流
            imageStream = processDiagramGenerator.generateDiagramCustom(
                    bpmnModel,
                    highLightedActivityIdList, runningActivityIdList, highLightedFlowsIds, runningActivityFlowsIds,
                    "宋体", "微软雅黑", "黑体");
            return imageStream;
        } catch (Exception e) {
            log.error("获取流程图时出现异常！", e.getMessage());
            throw new Exception("通过流程实例ID" + procInstId + "获取流程图时出现异常！", e);
        } finally {
            if (imageStream != null) {
                imageStream.close();
            }
        }
    }
    /**
     * 获取已经经过的路线
     * @param bpmnModel                    bpmnModel
     * @param historicActivityInstanceList historicActivityInstanceList
     * @return HighLightedFlows
     */
    public List<String> getHighLightedFlowsByIncomingFlows(BpmnModel bpmnModel,
                                                           List<HistoricActivityInstance> historicActivityInstanceList) {
        // 已经流经的顺序流，需要高亮显示
        List<String> highFlows = new ArrayList<>();

        // 全部活动节点(包括正在执行的和未执行的)
        List<FlowNode> allHistoricActivityNodeList = new ArrayList<>();

        //获取所有的历史节点
        //获取所有确定结束了的历史节点
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            // 获取流程节点
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            allHistoricActivityNodeList.add(flowNode);
        }
        // 循环活动节点
        for (FlowNode flowNode : allHistoricActivityNodeList) {
            // 获取每个活动节点的输入线
            List<SequenceFlow> incomingFlows = flowNode.getIncomingFlows();

            // 循环输入线，如果输入线的源头处于全部活动节点中，则将其包含在内
            for (SequenceFlow sequenceFlow : incomingFlows) {
                if (allHistoricActivityNodeList.stream().map(BaseElement::getId).collect(Collectors.toList()).contains(sequenceFlow.getSourceFlowElement().getId())) {
                    highFlows.add(sequenceFlow.getId());
                }
            }
        }
        return highFlows;
    }

    /**
     * 获取活动节点
     * @param bpmnModel
     * @param runningActivityIdList
     * @param historicActivityInstanceList
     * @return
     */
    public List<String> getRunningActivityFlowIdsByIcommingFlows(BpmnModel bpmnModel, List<String> runningActivityIdList, List<HistoricActivityInstance> historicActivityInstanceList) {
        List<String> runningActivityFlowsIds = new ArrayList<>();
        List<String> runningActivityIds = new ArrayList<>(runningActivityIdList);
        // 逆序寻找，因为historicActivityInstanceList有序
        if (CollectionUtils.isEmpty(runningActivityIds)) {
            return runningActivityFlowsIds;
        }

        // 全部活动节点(包括正在执行的和未执行的)
        List<FlowNode> allHistoricActivityNodeList = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            // 获取流程节点
            // bpmnModel.getMainProcess()获取一个Process对象
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            allHistoricActivityNodeList.add(flowNode);
        }

        for (int i = historicActivityInstanceList.size() - 1; i >= 0; i--) {
            HistoricActivityInstance historicActivityInstance = historicActivityInstanceList.get(i);
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            // 如果当前节点是未完成的节点
            if (runningActivityIds.contains(flowNode.getId())) {
                // 获取每个活动节点的输入线
                List<SequenceFlow> incomingFlows = flowNode.getIncomingFlows();

                // 循环输入线，如果输入线的源头处于全部活动节点中，则将其包含在内
                for (SequenceFlow sequenceFlow : incomingFlows) {
                    if (allHistoricActivityNodeList.stream().map(BaseElement::getId).collect(Collectors.toList()).contains(sequenceFlow.getSourceFlowElement().getId())) {
                        runningActivityFlowsIds.add(sequenceFlow.getId());
                    }
                }
            }

        }
        return runningActivityFlowsIds;
    }
}
