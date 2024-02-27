package com.activiti.z_six.service.manager;

import com.activiti.z_six.entity.process.FlowEntity;
import com.activiti.z_six.entity.taskAssignee.FlowElementAttrs;
import com.activiti.z_six.entity.taskAssignee.OvProcessInstance;
import com.activiti.z_six.mapper.processMapper.FlowEntityMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.FlowElementAttrsMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.OvProcessInstanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MapperManager {
    @Autowired
    private FlowElementAttrsMapper flowElementAttrsMapper;
    @Autowired
    private OvProcessInstanceMapper ovProcessInstanceMapper;
    @Autowired
    private FlowEntityMapper flowEntityMapper;

    /**
     * 获取最新流程模版信息
     * @param process_key
     * @return
     */
    public OvProcessInstance ovProcessInstance(String process_key){
        try{
            OvProcessInstance ovProcessInstance=ovProcessInstanceMapper.getProcessInsLastVersion(process_key);
            return ovProcessInstance;
        }
        catch (Exception ex){
            return null;
        }
    }

    /**
     * 流程发布，更新流程属性与节点属性
     * @param deploymentId
     * @return
     */
    public OvProcessInstance getProcessInsByDeployId(String deploymentId){
        try{
            OvProcessInstance ovProcessInstance=ovProcessInstanceMapper.getProcessInsByDeployId(deploymentId);
            return ovProcessInstance;
        }
        catch (Exception ex){
            return null;
        }
    }
    /**
     * 获取节点基本信息
     * @param task_def_key
     * @return
     */
    public FlowElementAttrs getFlowElementAttrs(String task_def_key){
        try{
            FlowElementAttrs flowElementAttrs=flowElementAttrsMapper.getFlowElementAttrs(task_def_key);
            return flowElementAttrs;
        }
        catch (Exception ex){
            return null;
        }
    }

    /**
     * 更新节点信息
     * @param taskElementAttrs
     */
    public void updateFlowElementAttrs(FlowElementAttrs taskElementAttrs){
        flowElementAttrsMapper.updateFlowElementAttrs(taskElementAttrs);
    }
    /**
     * 更新流程基本信息
     * @param flowEntity
     */
    public void updateFlowEntity(FlowEntity flowEntity){
        flowEntityMapper.updateFlowEntity(flowEntity);
    }

    /**
     * 更新标题生成规则
     * @param flowElementAttrs
     */
    public void updateTitleModel(FlowElementAttrs flowElementAttrs){
        flowElementAttrsMapper.updateTitleModel(flowElementAttrs);
    }
    /**
     * 更新节点信息
     * @param flowElementAttrs
     */
    public void setFlowElementAttrs(FlowElementAttrs flowElementAttrs){
        flowElementAttrsMapper.setFlowElementAttrs(flowElementAttrs);
    }

}
