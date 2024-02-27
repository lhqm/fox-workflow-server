package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.FlowElementAttrs;
import com.activiti.z_six.entity.process.FlowEnum;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FlowElementAttrsMapper {
    /**
     * 查询节点信息
     * @param task_def_id
     * @return
     */
    FlowElementAttrs getFlowElementAttrs(String task_def_id);

    /**
     * 查询流程信息
     * @param process_key
     * @return
     */
    List<FlowElementAttrs> getFlowElementFormAttrs(String process_key);
    /**
     * 设置节点信息
     * @param flowElementAttrs
     * @return
     */
    int setFlowElementAttrs(FlowElementAttrs flowElementAttrs);
    /**
     * 修改节点信息
     * @param flowElementAttrs
     * @return
     */
    int updateFlowElementAttrs(FlowElementAttrs flowElementAttrs);
    int updateFlowMap(FlowElementAttrs flowElementAttrs);

    /**
     * 标题生成规则
     * @param flowElementAttrs
     * @return
     */
    int updateTitleModel(FlowElementAttrs flowElementAttrs);

    /**
     * 更新节点属性
     * @param flowElementAttrs
     * @return
     */
    int updateTaskAttrs(FlowElementAttrs flowElementAttrs);
    List<FlowEnum> enumList();
}
